package com.sunfusheng.infostream.DataSource;

import java.util.List;

public interface DataPersist {

    void persist(List<Object> list, boolean isLoadMore);
}
