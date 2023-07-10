package com.benjtissot.sellingmugs;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class RouteTraceFilter extends Filter<ILoggingEvent> {

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMessage().contains(RoutePathsKt.PUSH_RESULT_PATH) || event.getMessage().contains("Trace for [click")) {
            return FilterReply.DENY;
        } else if (event.getMessage().contains("FAILURE @ /")){
            return FilterReply.ACCEPT;
        }else {
            return FilterReply.NEUTRAL;
        }
    }
}