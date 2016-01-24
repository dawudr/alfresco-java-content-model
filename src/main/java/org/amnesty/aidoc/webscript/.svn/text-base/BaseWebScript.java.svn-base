package org.amnesty.aidoc.webscript;

import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Common base class for all Aidoc webscripts that will:
 *  - catch all exceptions, log the details and rethrow them. This is because
 *    DeclarativeWebScript does not do this and we lose the exception in some cases
 *  - create a logger
 *    
 * @author chatch
 */
public abstract class BaseWebScript extends DeclarativeWebScript {
    
    protected Log logger = LogFactory.getLog(this.getClass());

    /*
     * Overrides DeclarativeWebScript to catch, log and rethrow any exceptions as
     * we can't see these in the logs.
     * 
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest,
     *      org.alfresco.web.scripts.WebScriptResponse)
     */
    final protected Map<String, Object> executeImpl(WebScriptRequest req,
            Status status) {
        Map<String, Object> model = null;
        try {
          model = executeAiImpl(req, status);
        } catch (Throwable e) {
            logger.error("webscript threw exception: ", e);
            throw new AlfrescoRuntimeException("webscript exception threw exception", e);
        }
        return model;
    }
    
    /**
     * Execute custom Java logic
     * 
     * @param req  Web Script request
     * @return  custom service model
     */
    protected abstract Map<String, Object> executeAiImpl(WebScriptRequest req, Status status);


    protected void handleError(int code, String message,
            Exception exception, Map<String, Object> model, Status status) {
        logger.error(message, exception);
        status.setMessage(message);
        status.setCode(code);
        
        model.put("message", status.getMessage());
        model.put("code", status.getCode());
    }

}
