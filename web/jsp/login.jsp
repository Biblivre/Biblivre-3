<%@page import="mercury.I18nUtils"%>
<%@page import="biblivre3.config.Config"%>
<%@page import="biblivre3.config.ConfigurationEnum"%>
<%@page import="biblivre3.config.ConfigurationDTO" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<i18n:setInitialLocale />

<layout:head>
    <style type="text/css">
        #content {
            background: #FFFFFF url('images/background2.jpg') repeat-y 50% 25%;
            font-weight: bold;
        }
    </style>
</layout:head>
	
<%
	ConfigurationDTO configDTO = new ConfigurationDTO();
	String lang = I18nUtils.getCurrentLanguage(session);
	String disclaimer = "";
	if (lang.equalsIgnoreCase("en_us")) {
		disclaimer = configDTO.getWelcome_disclaimer_en_us(false);
	} else if (lang.equalsIgnoreCase("es")) {
		disclaimer = configDTO.getWelcome_disclaimer_es(false);
	} else {
		disclaimer = configDTO.getWelcome_disclaimer_pt_br(false);
	}
	request.setAttribute("disclaimer", disclaimer);
%>

<layout:body thisPage="login">
    <div id="login_page_inner">
		${disclaimer}
    </div>
</layout:body>