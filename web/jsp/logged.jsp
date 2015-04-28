<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<i18n:setInitialLocale />

<layout:head> </layout:head>

<layout:body thisPage="logged">
    <div id="logged_page_inner">
        <h2><i18n:getText module="biblivre3" textKey="LOGGED_DISCLAIMER_1" />&nbsp;${LOGGED_USER.firstName},</h2>
        <i18n:getText module="biblivre3" textKey="LOGGED_DISCLAIMER_2" />
    </div>
</layout:body>
