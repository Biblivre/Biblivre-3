<%@page import="biblivre3.config.ConfigurationDTO" %>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<%
	ConfigurationDTO configDTO = new ConfigurationDTO();
	request.getSession().setAttribute("CONFIGURATION", configDTO);
	String disclaimer_en_us = configDTO.getWelcome_disclaimer_en_us(false);
	request.setAttribute("disclaimer_en_us", disclaimer_en_us);
	String disclaimer_es = configDTO.getWelcome_disclaimer_es(false);
	request.setAttribute("disclaimer_es", disclaimer_es);
	String disclaimer_pt_br = configDTO.getWelcome_disclaimer_pt_br(false);
	request.setAttribute("disclaimer_pt_br", disclaimer_pt_br);
%>
<i18n:setInitialLocale />

<layout:head>
    <link rel="stylesheet" media="screen" type="text/css" href="css/colorpicker.css" />
    <script type="text/javascript" src="scripts/colorpicker.js"></script>

    <script type="text/javascript">
        function updateExampleCss() {
            var c1 = $('#colorpicker_1').val();
            var c2 = $('#colorpicker_2').val();
            var c3 = $('#colorpicker_3').val();
            var c4 = $('#colorpicker_4').val();

            var params = "bordercolor=" + c1;
            params += "&headercolor=" + c2;
            params += "&bgcolor=" + c3;
            params += "&bglightcolor=" + c4;

            $('head').append('<link rel="stylesheet" media="screen" type="text/css" href="css/main.jsp?' + params + '" />');
        }

        $(document).ready(function(){
            var last;
            var timeout;
            $('#colorpicker_1, #colorpicker_2, #colorpicker_3, #colorpicker_4').ColorPicker({
                onChange: function(hsb, hex, rgb, el) {
                    $(last).val(hex);
                    clearTimeout(timeout);
                    timeout = setTimeout(updateExampleCss, 300);
                },
                onSubmit: function(hsb, hex, rgb, el) {
                    $(el).val(hex);
                    $(el).ColorPickerHide();
                    updateExampleCss();
                },
                onBeforeShow: function () {
                    $(this).ColorPickerSetColor(this.value);
                    last = this;
                }
            }).bind('keyup', function() {
                $(this).ColorPickerSetColor(this.value);
                if (this.value.length == 6) {
                    updateExampleCss();
                }
            });
        });

    </script>

    <style type="text/css">
        #admin_table {
            font-size: 14px;
            width: 100%;
        }

        #admin_table td, #admin_table th {
            border: 1px solid #000;
            padding: 4px;
        }

        #admin_table input {
            width: 180px;
        }
		
		.color_picker {
			width: 24px; 
			height: 24px;
			position: absolute;
			top: 50%;
			right: 7px;
			margin-top: -12px;
		}
		
		.disclaimer {
			width: 98%;
			height: 98%;
			vertical-align: middle;
		}
    </style>
</layout:head>

<layout:body thisPage="administration_configuration">
    <div class="spacer2"></div>
    <div class="submit_buttons">
        <button type="button" onclick="submitForm('FORM_1','CANCEL_CHANGES');"><i18n:getText module="biblivre3" textKey="BUTTON_CANCEL_CHANGES"/></button>
        <button type="button" onclick="submitForm('FORM_1','SAVE_CHANGES');"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE_CHANGES"/></button>
    </div>
    <div class="spacer2"></div>
    <table id="admin_table" border="0" cellspacing="0" cellpadding="2">
        <tr>
            <th width="30%"><i18n:getText module="biblivre3" textKey="LABEL_CONFIGURATION"/></th>
            <th width="35%"><i18n:getText module="biblivre3" textKey="LABEL_CURRENT_VALUE"/></th>
            <th width="35%"><i18n:getText module="biblivre3" textKey="LABEL_NEW_VALUE"/></th>
        </tr>
        <tr>
            <td><i18n:getText module="biblivre3" textKey="LABEL_LIBRARY_NAME"/></td>
            <td>${fn:escapeXml(CONFIGURATION.library_name)}</td>
            <td><input type="text" name="LIBRARY_NAME" value="${fn:escapeXml(CONFIGURATION.library_name)}" size="20"/></td>
        </tr>
        <tr>
            <td><i18n:getText module="biblivre3" textKey="LABEL_LIBRARY_SUBNAME"/></td>
            <td>${fn:escapeXml(CONFIGURATION.library_subname)}</td>
            <td><input type="text" name="LIBRARY_SUBNAME" value="${fn:escapeXml(CONFIGURATION.library_subname)}" size="20"/></td>
        </tr>
        <tr>
            <td><i18n:getText module="biblivre3" textKey="LABEL_MONEY_LOCALE"/></td>
            <td>${fn:escapeXml(CONFIGURATION.money_locale)}</td>
            <td><input type="text" name="MONEY_LOCALE" value="${fn:escapeXml(CONFIGURATION.money_locale)}" size="20"/></td>
        </tr>
        <tr>
            <td><i18n:getText module="biblivre3" textKey="LABEL_FINE_AMOUNT"/></td>
            <td>${fn:escapeXml(CONFIGURATION.fine_amount)}</td>
            <td><input type="text" name="FINE_AMOUNT" value="${fn:escapeXml(CONFIGURATION.fine_amount)}" size="20"/></td>
        </tr>
        <tr>
            <td><i18n:getText module="biblivre3" textKey="LABEL_BORDER_AND_TEXT_COLOR"/></td>
            <td style="position: relative;">${fn:escapeXml(CONFIGURATION.border_color)} <div class="color_picker" style="background-color: #${fn:escapeXml(CONFIGURATION.border_color)};">&nbsp;</div></td>
            <td><input type="text" name="BORDER_COLOR" value="${fn:escapeXml(CONFIGURATION.border_color)}" size="20" id="colorpicker_1"/></td>
        </tr>
        <tr>
            <td><i18n:getText module="biblivre3" textKey="LABEL_HEADER_BACKGROUND_COLOR"/></td>
            <td style="position: relative;">${fn:escapeXml(CONFIGURATION.header_color)} <div class="color_picker" style="background-color: #${fn:escapeXml(CONFIGURATION.header_color)};">&nbsp;</div></td>
            <td><input type="text" name="HEADER_COLOR" value="${fn:escapeXml(CONFIGURATION.header_color)}" size="20" id="colorpicker_2"/></td>
        </tr>
        <tr>
            <td><i18n:getText module="biblivre3" textKey="LABEL_BACKGROUND_COLOR"/></td>
            <td style="position: relative;">${fn:escapeXml(CONFIGURATION.background_color)} <div class="color_picker" style="background-color: #${fn:escapeXml(CONFIGURATION.background_color)};">&nbsp;</div></td>
            <td><input type="text" name="BACKGROUND_COLOR" value="${fn:escapeXml(CONFIGURATION.background_color)}" size="20" id="colorpicker_3"/></td>
        </tr>
        <tr>
            <td><i18n:getText module="biblivre3" textKey="LABEL_BACKGROUND_LIGHT_COLOR"/></td>
            <td style="position: relative;">${fn:escapeXml(CONFIGURATION.background_light_color)} <div class="color_picker" style="background-color: #${fn:escapeXml(CONFIGURATION.background_light_color)};">&nbsp;</div></td>
            <td><input type="text" name="BACKGROUND_LIGHT_COLOR" value="${fn:escapeXml(CONFIGURATION.background_light_color)}" size="20" id="colorpicker_4"/></td>
        </tr>
        <tr>
            <td><i18n:getText module="biblivre3" textKey="LABEL_RECORDS_PER_PAGE"/></td>
            <td>${fn:escapeXml(CONFIGURATION.recordsPerPage)}</td>
            <td><input type="text" name="RECORDS_PER_PAGE" value="${fn:escapeXml(CONFIGURATION.recordsPerPage)}" size="20"/></td>
        </tr>
        <tr>
            <td><i18n:getText module="biblivre3" textKey="LABEL_DIGITAL_MEDIA"/></td>
            <td>${fn:escapeXml(CONFIGURATION.digitalMedia)}</td>
            <td><input type="text" name="DIGITAL_MEDIA" value="${fn:escapeXml(CONFIGURATION.digitalMedia)}" size="20"/></td>
        </tr>
        <tr>
            <td><i18n:getText module="biblivre3" textKey="LABEL_ASSET_PREFIX"/></td>
            <td>${fn:escapeXml(CONFIGURATION.assetPrefix)}</td>
            <td><input type="text" name="ASSET_PREFIX" value="${fn:escapeXml(CONFIGURATION.assetPrefix)}" size="20"/></td>
        </tr>
        <tr>
            <td><i18n:getText module="biblivre3" textKey="LABEL_WELCOME_DISCLAIMER_PT_BR"/></td>
            <td>${disclaimer_pt_br}</td>
            <td><textarea class="disclaimer" name="WELCOME_DISCLAIMER_PT_BR"/>${fn:escapeXml(CONFIGURATION.welcome_disclaimer_pt_br)}</textarea></td>
        </tr>
        <tr>
            <td><i18n:getText module="biblivre3" textKey="LABEL_WELCOME_DISCLAIMER_EN_US"/></td>
            <td>${disclaimer_en_us}</td>
            <td><textarea class="disclaimer" name="WELCOME_DISCLAIMER_EN_US"/>${fn:escapeXml(CONFIGURATION.welcome_disclaimer_en_us)}</textarea></td>
        </tr>
        <tr>
            <td><i18n:getText module="biblivre3" textKey="LABEL_WELCOME_DISCLAIMER_ES"/></td>
            <td>${disclaimer_es}</td>
            <td><textarea class="disclaimer" name="WELCOME_DISCLAIMER_ES"/>${fn:escapeXml(CONFIGURATION.welcome_disclaimer_es)}</textarea></td>
        </tr>		
    </table>
</layout:body>

