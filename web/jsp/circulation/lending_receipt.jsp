<%@page import="java.text.DecimalFormat"%>
<%@page import="mercury.I18nUtils"%>
<%@ page import="biblivre3.utils.TextUtils" %>
<%@ page import="mercury.DTOCollection" %>
<%@ page import="biblivre3.circulation.lending.LendingInfoDTO" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<i18n:setInitialLocale />

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
        <meta http-equiv="Cache-Control" content="no-Cache" >

        <meta http-equiv="Pragma" content="no-Cache" >
    </head>
    <%
        DTOCollection<LendingInfoDTO> lendingList = (DTOCollection<LendingInfoDTO>)request.getAttribute("LENDING_RECEIPT_LENDING_LIST");
        DTOCollection<LendingInfoDTO> renewList = (DTOCollection<LendingInfoDTO>)request.getAttribute("LENDING_RECEIPT_RENEW_LIST");
        DTOCollection<LendingInfoDTO> returnList = (DTOCollection<LendingInfoDTO>)request.getAttribute("LENDING_RECEIPT_RETURN_LIST");

        boolean hasLendings = (lendingList != null && lendingList.getList().size() > 0);
        boolean hasRenews = (renewList != null && renewList.getList().size() > 0);
        boolean hasReturns = (returnList != null && returnList.getList().size() > 0);

        SimpleDateFormat sdf = new SimpleDateFormat(I18nUtils.getText(session, "biblivre3", "DEFAULT_DATE_FORMAT"));
        DecimalFormat curr  = new DecimalFormat("#,###.00");
    %>
    <body>
<pre>
************************
*                      *
* <%= TextUtils.center((String) request.getAttribute("LENDING_BIBLIO_NAME"), 20) %> *
*                      *
************************
<%= TextUtils.center((new SimpleDateFormat(I18nUtils.getText(session, "biblivre3", "DEFAULT_DATETIME_FORMAT"))).format(new java.util.Date()), 24) %>

<i18n:getText module="biblivre3" textKey="RECEIPT_READER_NAME" />:
   <%= TextUtils.wraptext((String) request.getAttribute("LENDING_READER_NAME"), 21, "\n   ") %>
<i18n:getText module="biblivre3" textKey="RECEIPT_READER_ENROL" />:
   <%= (Integer) request.getAttribute("LENDING_READER_ENROL") %>
</pre>

<% if (hasLendings) { try { %>
<pre><i18n:getText module="biblivre3" textKey="RECEIPT_LENT_DESCRIPTION" /></pre>
<% for (LendingInfoDTO item : lendingList.getList()) { %>

<pre>**********
<i18n:getText module="biblivre3" textKey="RECEIPT_AUTHOR" />:
   <%= TextUtils.wraptext(item.getAuthor(), 21, "\n   ") %>
<i18n:getText module="biblivre3" textKey="RECEIPT_TITLE" />:
   <%= TextUtils.wraptext(item.getTitle(), 21, "\n   ") %>
<i18n:getText module="biblivre3" textKey="RECEIPT_BIBLIO" />:
   <%= item.getSerial() %>
<i18n:getText module="biblivre3" textKey="RECEIPT_HOLDING" />:
   <%= item.getAssetHolding() %>
<i18n:getText module="biblivre3" textKey="RECEIPT_LIMIT_LEND_DATE" />:
   <%= sdf.format(item.getReturnDate()) %>
**********</pre>
<br>
<% }
} catch(Exception e) {}} %>

<% if (hasRenews) { try { %>
<pre><i18n:getText module="biblivre3" textKey="RECEIPT_RENEW_DESCRIPTION" /></pre>
<% for (LendingInfoDTO item : renewList.getList()) { %>

<pre>**********
<i18n:getText module="biblivre3" textKey="RECEIPT_AUTHOR" />:
   <%= TextUtils.wraptext(item.getAuthor(), 21, "\n   ") %>
<i18n:getText module="biblivre3" textKey="RECEIPT_TITLE" />:
   <%= TextUtils.wraptext(item.getTitle(), 21, "\n   ") %>
<i18n:getText module="biblivre3" textKey="RECEIPT_BIBLIO" />:
   <%= item.getSerial() %>
<i18n:getText module="biblivre3" textKey="RECEIPT_HOLDING" />:
   <%= item.getAssetHolding() %>
<i18n:getText module="biblivre3" textKey="RECEIPT_LIMIT_LEND_DATE" />:
   <%= sdf.format(item.getReturnDate()) %>
**********</pre>
<br>
<% }
} catch(Exception e) {}} %>

<% if (hasReturns) { try { %>
<pre><i18n:getText module="biblivre3" textKey="RECEIPT_RETURN_DESCRIPTION" /></pre>
<% for (LendingInfoDTO item : returnList.getList()) { %>

<pre>**********
<i18n:getText module="biblivre3" textKey="RECEIPT_AUTHOR" />:
   <%= TextUtils.wraptext(item.getAuthor(), 21, "\n   ") %>
<i18n:getText module="biblivre3" textKey="RECEIPT_TITLE" />:
   <%= TextUtils.wraptext(item.getTitle(), 21, "\n   ") %>
<i18n:getText module="biblivre3" textKey="RECEIPT_BIBLIO" />:
   <%= item.getSerial() %>
<i18n:getText module="biblivre3" textKey="RECEIPT_HOLDING" />:
   <%= item.getAssetHolding() %>
<i18n:getText module="biblivre3" textKey="RECEIPT_LEND_DATE" />:
   <%= (new SimpleDateFormat("dd/MM/yyyy")).format(item.getLendDate()) %>
<i18n:getText module="biblivre3" textKey="RECEIPT_RETURN_DATE" />:
   <%= sdf.format(item.getReturnDate()) %>
<% if (item.getFineValue() != null && item.getFineValue() > 0) { %>** <i18n:getText module="biblivre3" textKey="LABEL_FINE_AMOUNT" />:
   ** <%= curr.format(item.getFineValue()) %><% if (item.isFinePaid()) { %> <i18n:getText module="biblivre3" textKey="RECEIPT_PAID" /><% }} %>
**********</pre>
<br>
<% }
} catch(Exception e) {}} %>

<pre>************************</pre>
    </body>
</html>