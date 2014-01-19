package com.uniwin.webkey.system.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;

public class GenericLoggerBean
{

    public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable
    {
        Logger logger = LogManager.getLogger(joinPoint.getTarget().getClass());
        logger.info(joinPoint.getSignature().getName() + "()");
        long startTime = System.currentTimeMillis();
        Exception e3 = null;
        try
        {
            Object result = joinPoint.proceed();
            return result;
        } catch (Exception e)
        {
            logger.error(joinPoint.getTarget().getClass() + "."
                    + joinPoint.getSignature().getName() + "() invoke error");
            logger.error(org.zkoss.util.resource.Labels
                    .getLabel("log.ui.errorInfo")
                    + " [" + e.getMessage() + "]");
            e3 = e;

        } finally
        {
            logger.info(org.zkoss.util.resource.Labels
                    .getLabel("log.ui.exeuemthod")
                    + joinPoint.getTarget().getClass()
                    + "."
                    + joinPoint.getSignature().getName() + "()");
            logger.info(org.zkoss.util.resource.Labels
                    .getLabel("log.ui.method")
                    + " invocation "
                    + org.zkoss.util.resource.Labels
                            .getLabel("system.commom.ui.time")
                    + (System.currentTimeMillis() - startTime) + " ms.");
        }
        if (e3 != null)
        {
            throw e3;
        }
        return null;
    }
}