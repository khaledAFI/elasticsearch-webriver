package com.klead.es.river.aspect;

import com.klead.es.common.AppContext;
import com.klead.es.common.EsAdminService;
import com.klead.es.common.RiverMailer;
import com.klead.es.river.IndexationCommand;
import com.klead.es.river.IndexationResult;
import com.klead.es.river.WorkReport;
import com.klead.es.river.WorkerReport;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Created by kafi on 23/02/2016.
 */
@Aspect
@Component
public class ReportingAspect {
    private static final Logger LOGGER = Logger.getLogger(ReportingAspect.class);
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILURE = "FAILURE";

    @Autowired
    private EsAdminService esAdminService;
    @Autowired
    private RiverMailer riverMailer ;

    @Pointcut("execution(* com.klead.es.river.strategy.IndexationStrategy+.index(..))")
    public void reportingMethod() {

    }

    @Around("reportingMethod()")
    public Object aroundReportingMethod(ProceedingJoinPoint pjp) {
        long start = System.currentTimeMillis();
        LOGGER.debug("Start indexation process.");
        IndexationResult indexationResult = new IndexationResult();
        try {
            indexationResult = (IndexationResult) pjp.proceed();
            // get the work report
            WorkReport workReport = AppContext.getApplicationContext().getBean("workReport", WorkReport.class);
            parseWorkReport(workReport, indexationResult, start,(IndexationCommand) pjp.getArgs()[0]);
            riverMailer.sendPreConfiguredSuccessMail("SUCCESS");
        } catch (Throwable e) {
            LOGGER.error("Error indexation process.", e);
            riverMailer.sendPreConfiguredFailMail("FAILURE");
        } finally {
            LOGGER.debug("End  indexation process.");
        }
        return indexationResult;
    }

    private void parseWorkReport(WorkReport workReport, IndexationResult indexationResult, long start, IndexationCommand command) {
        final Collection<WorkerReport> workerReports = workReport.getReport().values();
        Long totalFailureDocsCount = 0L;
        Long totalSuccessDocsCount = 0L;
        for(WorkerReport report : workerReports){
            if (report != null){
                if (SUCCESS.equals(report.getStatus())){
                    totalSuccessDocsCount += report.getDocsCount();
                }else if (FAILURE.equals(report.getStatus())){
                    totalFailureDocsCount+= report.getDocsCount();
                }
            }
        }

        indexationResult.setTotalSuccessDocsCount(totalSuccessDocsCount);
        indexationResult.setTotalFailureDocsCount(totalFailureDocsCount);
        indexationResult.setTotalExecutionTime(System.currentTimeMillis()-start);
        try {
            indexationResult.setEsReport( esAdminService.indexStatistics(command.getIndexName()));
        } catch (Exception e) {
            LOGGER.error("Error getting Es statistics.", e);
        }
        indexationResult.setWorkReport(workReport);
    }
}
