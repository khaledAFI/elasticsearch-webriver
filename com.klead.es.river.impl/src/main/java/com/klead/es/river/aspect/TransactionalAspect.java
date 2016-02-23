package com.klead.es.river.aspect;

import com.klead.es.common.AppContext;
import com.klead.es.river.WorkReport;
import com.klead.es.river.WorkerReport;
import com.klead.es.river.data.Document;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by kafi on 22/02/2016.
 */
@Aspect
@Component
public class TransactionalAspect {


    private static final Logger LOGGER = Logger.getLogger(TransactionalAspect.class);
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILURE = "FAILURE";
    @Autowired
    private DataSourceTransactionManager txManager;


    @Pointcut("execution(* com.klead.es.river.worker.MysqlWorker.run(..))")
    public void transactionalMethod() {
    }

    @Around("transactionalMethod()")
    public Object aroundTransactionalMethod(ProceedingJoinPoint joinPoint) {
        long start = System.currentTimeMillis();
        WorkReport workReport = null;
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = txManager.getTransaction(def);
        try {
            workReport = (WorkReport) AppContext.getApplicationContext().getBean("workReport");
            joinPoint.proceed();
            long ends = System.currentTimeMillis();
            addWorkerReport(joinPoint, SUCCESS, ends - start, workReport);
            txManager.commit(status);

        } catch (Throwable th) {
            long ends = System.currentTimeMillis();
            addWorkerReport(joinPoint, FAILURE, ends - start, workReport);
            txManager.rollback(status);
        }
        return null;
    }

    private void addWorkerReport(JoinPoint joinPoint, String status, long executionTime, WorkReport workReport) {
        try {
            Object target = joinPoint.getTarget();
            Class clazz = target.getClass();
            Field fDocuments = clazz.getDeclaredField("documents");
            Field fId = clazz.getDeclaredField("id");
            fDocuments.setAccessible(true);
            fId.setAccessible(true);
            List<Document> documents = (List<Document>) fDocuments.get(target);
            Long id = (Long) fId.get(target);
            WorkerReport workerReport = new WorkerReport();
            workerReport.setDocsCount(Long.valueOf(documents.size()));
            workerReport.setId(id);
            workerReport.setStatus(status);
            workerReport.setWorkerExecutionTime(executionTime);
            workReport.getReport().put(id, workerReport);
            LOGGER.info("WORKER[" + id + "] " + status);
        } catch (Exception e) {
            LOGGER.error("TransactionalAspect parse error");
        }
    }

}
