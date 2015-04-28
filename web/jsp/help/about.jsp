<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<i18n:setInitialLocale />

<layout:head>
    <style type="text/css">
        #content_inner {
            margin: 20px 20px 20px 20px;
            line-height: 18px;
        }

        #content_inner p {
            font-size: 12px;
            line-height: 16px;
        }

        #content_inner li {
            font-size: 12px;
            line-height: 16px;
        }

        hr {
            text-align: center;
        }
    </style>

</layout:head>

<layout:body thisPage="help_about">
    <i18n:loadLocalizedFile filename="about"/>
</layout:body>
