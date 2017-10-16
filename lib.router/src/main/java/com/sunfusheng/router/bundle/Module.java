package com.sunfusheng.router.bundle;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.sunfusheng.router.Router;
import com.sunfusheng.router.annotation.WebRequest;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;

public class Module {
    private static final String TAG = "Router.Module";

    public static class ArgumentInfo {
        public static class ArgType {
            final static public int TYPE_UNKNOWN = -1;
            final static public int TYPE_STRING = 0;
            final static public int TYPE_INTEGER = 1;
            final static public int TYPE_BOOLEAN = 2;

            public static int getArgType(String typeString) {
                if ("string".equalsIgnoreCase(typeString)) {
                    return TYPE_STRING;
                } else if ("integer".equalsIgnoreCase(typeString) || "int".equalsIgnoreCase(typeString)) {
                    return TYPE_INTEGER;
                } else if ("boolean".equalsIgnoreCase(typeString) || "bool".equalsIgnoreCase(typeString)) {
                    return TYPE_BOOLEAN;
                } else {
                    return TYPE_UNKNOWN;
                }
            }
        }

        private String argName;
        private int argType;
        private boolean optional = false;
        private boolean urlEncode = false;

        public String getArgName() {
            return argName;
        }

        public int getArgType() {
            return argType;
        }

        public boolean isOptional() {
            return optional;
        }

        public boolean isUrlEncode() {
            return urlEncode;
        }
    }

    static class ModuleItem {
        private String moduleItemType;
        private String packageName;
        private String moduleItemName;
        private String classForPath;
        private String[] open_url;
        private Map<String, ArgumentInfo> argMap = new HashMap<>();
        private List<String> requiredArgs = new ArrayList<>();

        ModuleItem(String packageName) {
            this.packageName = packageName;
        }

        String getModuleItemName() {
            return moduleItemName;
        }

        String getClassName() {
            if (classForPath.startsWith(".")) {
                return packageName + classForPath;
            } else {
                return classForPath;
            }
        }

        void addArgInfo(ArgumentInfo argumentInfo) {
            argMap.put(argumentInfo.getArgName(), argumentInfo);
            if (!argumentInfo.isOptional()) {
                requiredArgs.add(argumentInfo.getArgName());
            }
        }

        ArgumentInfo getArgInfo(String queryName) {
            return argMap.get(queryName);
        }

        List<String> checkRequiredArgs(Bundle bundle) {
            List<String> missingArgList = new ArrayList<>();
            for (String query : requiredArgs) {
                if (!bundle.containsKey(query)) {
                    missingArgList.add(query);
                }
            }
            return missingArgList;
        }
    }

    private String moduleName;
    private String packageName;
    private Map<String, Map<String, ModuleItem>> moduleItemMap = new HashMap<>();
    private Map<String, ModuleItem> moduleUrlMap = new HashMap<>();
    private Map<String, Method> webRequestMap = new HashMap<>();

    private Module() {
    }

    static Module parseBundle(Node bundleNode) {
        try {
            Module module = new Module();

            NamedNodeMap bundleAttributes = bundleNode.getAttributes();
            module.moduleName = bundleAttributes.getNamedItem("name").getNodeValue();
            module.packageName = bundleAttributes.getNamedItem("package").getNodeValue();

            NodeList childNodes = bundleNode.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                if ("initializer".equals(child.getNodeName()))
                    continue;

                ModuleItem item = parseModuleItem(module.packageName, child);
                if (item == null) {
                    continue;
                }
                module.addModuleItem(item);
                module.addModuleUrlItem(item);
            }

            return module;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ModuleItem parseModuleItem(String packageName, Node moduleItemNode) {
        try {
            ModuleItem item = new ModuleItem(packageName);
            item.moduleItemType = moduleItemNode.getNodeName();
            NamedNodeMap attributes = moduleItemNode.getAttributes();

            if (attributes == null) {
                return null;
            }

            item.classForPath = attributes.getNamedItem("class").getNodeValue();

            if (attributes.getNamedItem("name") != null) {
                item.moduleItemName = attributes.getNamedItem("name").getNodeValue();
            }
            if (attributes.getNamedItem("url") != null) {
                item.open_url = attributes.getNamedItem("url").getNodeValue().split(",");
            }

            NodeList childNodes = moduleItemNode.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                if (!"arg".equals(child.getNodeName())) {
                    continue;
                }

                ArgumentInfo argumentInfo = parseArgumentInfo(child);
                if (argumentInfo == null || argumentInfo.getArgName() == null) {
                    Log.e(TAG, "Invalid arg node in bundle.xml!");
                    continue;
                }

                item.addArgInfo(argumentInfo);
            }
            return item;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    private static ArgumentInfo parseArgumentInfo(Node argumentInfoNode) {
        try {
            ArgumentInfo argumentInfo = new ArgumentInfo();
            NamedNodeMap argumentAttributes = argumentInfoNode.getAttributes();
            argumentInfo.argName = argumentAttributes.getNamedItem("name").getNodeValue();

            argumentInfo.argType = ArgumentInfo.ArgType.TYPE_STRING;
            if (argumentAttributes.getNamedItem("type") != null) {
                argumentInfo.argType = ArgumentInfo.ArgType.getArgType(argumentAttributes.getNamedItem("type").getNodeValue());
            }

            argumentInfo.optional = false;
            if (argumentAttributes.getNamedItem("optional") != null) {
                argumentInfo.optional = "true".equalsIgnoreCase(argumentAttributes.getNamedItem("optional").getNodeValue());
            }

            if (argumentAttributes.getNamedItem("url_encode") != null) {
                argumentInfo.urlEncode = "true".equalsIgnoreCase(argumentAttributes.getNamedItem("url_encode").getNodeValue());
            }
            return argumentInfo;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    String getModuleName() {
        return moduleName;
    }

    private void addModuleItem(ModuleItem item) {
        if ("web_request".equals(item.moduleItemType)) {
            return;
        }

        if (moduleItemMap.get(item.moduleItemType) == null) {
            moduleItemMap.put(item.moduleItemType, new HashMap<String, ModuleItem>());
        }
        moduleItemMap.get(item.moduleItemType).put(item.getModuleItemName(), item);
    }

    private void addModuleUrlItem(ModuleItem item) {
        if (item.open_url == null) {
            return;
        }

        for (int i = 0; i < item.open_url.length; i++) {
            String url = item.open_url[i].toLowerCase().trim();
            if (moduleUrlMap.get(url) == null) {
                moduleUrlMap.put(url, item);
            }
        }
    }

    ModuleItem getModuleItem(String type, String moduleName) {
        if (moduleItemMap.get(type) == null) {
            return null;
        }

        return moduleItemMap.get(type).get(moduleName);
    }

    ModuleItem getModuleUrlItem(String url) {
        return moduleUrlMap.get(url);
    }

    Set<String> getUrlsRegistered() {
        return moduleUrlMap.keySet();
    }

    Observable<String> invokeUrl(Context context, Object handlerInstance, String path, Bundle bundle) {
        ModuleItem moduleItem = moduleUrlMap.get(path);
        if (moduleItem == null) {
            return Observable.just(null);
        }

        if ("activity".equals(moduleItem.moduleItemType)) {
            invokeActivityUrl(context, moduleItem, bundle);
            return Observable.just(null);
        } else if ("web_request".equals(moduleItem.moduleItemType)) {
            return invokeWebRequestUrl(context, handlerInstance, moduleItem, path, bundle);
        }

        return Observable.just(null);
    }

    private void invokeActivityUrl(Context context, ModuleItem moduleItem, Bundle bundle) {
        Router.startActivity(context, getModuleName(), moduleItem.getModuleItemName(), bundle);
    }

    @SuppressWarnings("unchecked")
    private Observable<String> invokeWebRequestUrl(Context context, Object handlerInstance, ModuleItem moduleItem, String path, Bundle bundle) {
        try {
            Method methodForRequest = webRequestMap.get(path);

            if (methodForRequest == null) {
                Class handlerClazz = Class.forName(moduleItem.getClassName());
                Method[] methods = handlerClazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (!method.isAnnotationPresent(WebRequest.class)) {
                        continue;
                    }

                    String methodPath = method.getAnnotation(WebRequest.class).value();
                    if (TextUtils.isEmpty(methodPath)) {
                        continue;
                    }

                    if (!methodPath.toLowerCase().equals(path.toLowerCase())) {
                        continue;
                    }

                    if (handlerInstance != null && !handlerInstance.getClass().equals(handlerClazz)) {
                        handlerInstance = null;
                    }

                    if (handlerInstance == null && !Modifier.isStatic(method.getModifiers())) {
                        throw new RuntimeException("No handler specified for \"" + path + "\" is non-static method!");
                    }

                    webRequestMap.put(path, method);
                    methodForRequest = method;
                    break;
                }
            }

            if (methodForRequest == null) {
                Log.e(TAG, "No handler found for web_request url \"" + path + "\"");
                return Observable.just(null);
            }

            methodForRequest.setAccessible(true);
            Object result = methodForRequest.invoke(handlerInstance, context, bundle);
            if (result instanceof Observable) {
                return (Observable) result;
            } else if (result instanceof String) {
                return Observable.just((String) result);
            } else {
                return Observable.just(null);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return Observable.just(null);
    }
}
