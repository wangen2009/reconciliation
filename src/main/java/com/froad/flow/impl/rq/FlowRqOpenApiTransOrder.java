package com.froad.flow.impl.rq;

import com.froad.beans.Rckflowdetail;
import com.froad.comon.util.DateTime;
import com.froad.comon.util.PropertyUtil;
import com.froad.flow.FlowException;
import com.froad.flow.FlowInterface;
import com.froad.flow.service.RqService;
import com.froad.comon.util.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 日切openapi.trans_order流程
 */
@Component("flowRqOpenApiTransOrder")
public class FlowRqOpenApiTransOrder implements FlowInterface {
    private final static Logger logger = Logger.getLogger(FlowRqOpenApiTransOrder.class);
    private static boolean locked = false;
    @Autowired
    private RqService rqService;

    public Rckflowdetail execute(Rckflowdetail rckflowdetail) throws FlowException {
        logger.info("日切openapi.trans_order流程开始!");
        if (!locked) {
            locked = true;
            String cleardate = rckflowdetail.getId().getCleardate();
            logger.info("日切时间:" + cleardate);
            try {
            	int rqDate = Integer.parseInt(PropertyUtil.getProperties("RqDate"));
                String startDate = DateTime.get14StrDayBeforeForDate(cleardate, rqDate);
                String endDate = DateTime.get14StrDayBeforeForDate(cleardate, (rqDate - 1));
                int insertNum = rqService.rqOpenApiTransOrder(startDate, endDate);
                rckflowdetail.setDealcount(insertNum);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
                throw new FlowException(rckflowdetail, e);
            } finally {
                locked = false;
            }
        } else {
            logger.error("openapi.trans_order交易日切已在运行");
            throw new FlowException(rckflowdetail, "openapi.trans_order交易日切正在运行......");
        }
        return rckflowdetail;
    }
}
