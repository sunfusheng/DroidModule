package com.sunfusheng.utils.permission;

import java.util.List;

/**
 * @author sunfusheng on 2017/8/21.
 */
public interface IPermissionCallback {

    void onCheckPermission(List<String> failedPermissions);
}
