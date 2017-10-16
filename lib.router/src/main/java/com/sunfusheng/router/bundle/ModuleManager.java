package com.sunfusheng.router.bundle;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.sunfusheng.router.Router;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;

import io.reactivex.Observable;

public class ModuleManager {
    private static final String TAG = "Router.ModuleManager";
    private static final String BUNDLE_SUFFIX = ".bundle.xml";
    private static Map<String, Module> moduleMap;
    private static Map<String, Module> moduleUrlMap;

    private static class Holder {
        private static ModuleManager instance = new ModuleManager();
    }

    public static ModuleManager getInstance() {
        return Holder.instance;
    }

    public void loadBundles(Context context) {
        if (moduleMap != null) {
            return;
        }

        moduleMap = new HashMap<>();
        moduleUrlMap = new HashMap<>();

        try {
            String[] bundleList = context.getAssets().list("");
            for (String bundleFile : bundleList) {
                if (bundleFile.endsWith(BUNDLE_SUFFIX)) {
                    loadBundleFile(context, bundleFile);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void loadBundleFile(Context context, String bundlePath) {
        InputStream bundleStream = null;
        try {
            bundleStream = context.getAssets().open(bundlePath);
            parseBundles(context, bundleStream);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            try {
                if (bundleStream != null) {
                    bundleStream.close();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private void parseBundles(final Context context, InputStream bundleStream) {
        try {
            final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(bundleStream);
            registerModuleItems(document);
            new Handler().post(new Runnable() {
                //TODO:需要等待application onCreate之后
                @Override
                public void run() {
                    initModule(context, document);
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void initModule(Context context, Document document) {
        NodeList initializerNodeList = document.getElementsByTagName("initializer");
        if (initializerNodeList.getLength() < 1) {
            return;
        }

        try {
            Node initializerNode = initializerNodeList.item(0);
            NamedNodeMap initializerNodeAttributes = initializerNode.getAttributes();
            Class initializerClass = Class.forName(initializerNodeAttributes.getNamedItem("name").getNodeValue());
            Router.Initializer initializer = (Router.Initializer) initializerClass.newInstance();
            initializer.onInit(context);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void registerModuleItems(Document document) {
        NodeList bundleList = document.getElementsByTagName("module");
        for (int i = 0; i < bundleList.getLength(); i++) {
            Module module = Module.parseBundle(bundleList.item(i));
            if (module == null || module.getModuleName() == null) {
                Log.e(TAG, "Invalid module node in bundle.xml!");
                continue;
            }

            if (!moduleMap.containsKey(module.getModuleName())) {
                moduleMap.put(module.getModuleName(), module);
            }

            Set<String> urlsRegistered = module.getUrlsRegistered();
            if (urlsRegistered.size() > 0) {
                for (String url : urlsRegistered) {
                    moduleUrlMap.put(url, module);
                }
            }
        }
    }

    public String getClass(String moduleName, String type, String itemName) {
        Module module = moduleMap.get(moduleName);
        if (module == null) {
            return null;
        }

        Module.ModuleItem moduleItem = module.getModuleItem(type, itemName);
        if (moduleItem == null) {
            return null;
        }

        return moduleItem.getClassName();
    }

    public boolean checkRequiredArgs(String moduleName, String type, String method, Bundle bundle) {
        Module module = moduleMap.get(moduleName);
        if (module == null) {
            return false;
        }

        Module.ModuleItem methodInfo = module.getModuleItem(type, method);
        if (methodInfo == null) {
            return false;
        }

        List<String> missingArgs = methodInfo.checkRequiredArgs(bundle);
        if (missingArgs.size() != 0) {
            Log.e(TAG, "Parameter " + missingArgs + " missed for module: " + moduleName + ", method: " + method);
            return false;
        }

        return true;
    }

    public Observable<String> invokeUrl(Context context, Object handlerInstance, String openUrl) {
        Uri requestURI = Uri.parse(openUrl);

        if (requestURI == null || requestURI.getPath() == null) {
            return Observable.just(null);
        }

        String path = requestURI.getPath().toLowerCase();
        Module module = moduleUrlMap.get(path);
        if (module == null) {
            return Observable.just(null);
        }

        Module.ModuleItem moduleItem = module.getModuleUrlItem(path);
        if (moduleItem == null) {
            return Observable.just(null);
        }

        Bundle bundle = new Bundle();
        for (String queryName : requestURI.getQueryParameterNames()) {
            Module.ArgumentInfo argInfo = moduleItem.getArgInfo(queryName);
            if (argInfo == null) {
                bundle.putString(queryName, requestURI.getQueryParameter(queryName));
                continue;
            }

            switch (argInfo.getArgType()) {
                case Module.ArgumentInfo.ArgType.TYPE_STRING:
                    String argValue = requestURI.getQueryParameter(queryName);
                    if (argInfo.isUrlEncode()) {
                        argValue = URLEncoder.encode(argValue);
                    }
                    bundle.putString(queryName, argValue);
                    break;
                case Module.ArgumentInfo.ArgType.TYPE_BOOLEAN:
                    String stringVal = requestURI.getQueryParameter(queryName).toLowerCase();
                    boolean booleanVal = "1".equals(stringVal) || "true".equals(stringVal);
                    bundle.putBoolean(queryName, booleanVal);
                    break;
                case Module.ArgumentInfo.ArgType.TYPE_INTEGER:
                    try {
                        bundle.putInt(queryName, Integer.valueOf(requestURI.getQueryParameter(queryName)));
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    Log.e(TAG, "Invalid argument type.");
                    break;
            }
        }

        bundle.putString("open_url", URLEncoder.encode(openUrl));

        return module.invokeUrl(context, handlerInstance, path, bundle);
    }
}
