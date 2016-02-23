package com.klead.es.river.factory;

import com.klead.es.common.AppContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * Created by kafi on 16/02/2016.
 */
@Component
public class TaskExecutorFactory {

    public ThreadPoolTaskExecutor getTaskExecutor(int corePoolSize, int maxPoolSize, int queueCapacit) {
        ThreadPoolTaskExecutor taskExecutor =(ThreadPoolTaskExecutor) AppContext.getApplicationContext().getBean("taskExecutor");
        taskExecutor.setCorePoolSize(corePoolSize);
        taskExecutor.setMaxPoolSize(maxPoolSize);
        taskExecutor.setQueueCapacity(queueCapacit);
        return taskExecutor;
    }
}
