<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@page import="biblivre3.enums.Database" %>
<%@taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>

<i18n:setInitialLocale />

<layout:head>
    <script type="text/javascript" src="scripts/circulation.js"></script>
    <script type="text/javascript" src="scripts/reports.js"></script>

    <style type="text/css">
        .hiddenReports {
            display: none;
        }
    </style>
    <script type="text/javascript">
        function createUserReport(id, name) {
            if (confirm(Translations.CONFIRM_USER_REPORT(name))) {
                $('#userId').val(id);
                submitForm('FORM_1', 'GENERATE_REPORT');
            }
        }

        function createAuthorReport(ids, name) {
            if (confirm(Translations.CONFIRM_AUTHOR_REPORT(name))) {
                $('#recordIds').val(ids);
                $('#authorName').val(name);
                submitForm('FORM_1', 'GENERATE_REPORT');
            }
        }

        function toggleDivs() {
            $('#dateDiv, #buttonDiv, #orderDiv, #userDiv, #authorDiv, #databaseSelection, #deweyDiv').hide();
            var report = $('#reportId').val('');
            var reportId = $('#reportSelect').val();

            if (reportId !== '') {
                $('#buttonDiv').show();
                report.val(reportId);
            }

            if (reportId === "1" || reportId === "4" || reportId === "9" || reportId === "10" || reportId === "16") {
                $('#dateDiv').show();
            }

            if (reportId === "2") {
                $('#orderDiv').show();
                $('#databaseSelection').show();
            }

            if (reportId === "3") {
                $('#databaseSelection').show();
                $('#deweyDiv').show();
            }

            if (reportId === "6") {
                $('#userDiv').show();
                $('#buttonDiv').hide();
            }

            if (reportId === "5") {
                $('#authorDiv').show();
                $('#buttonDiv').hide();
            }

        }
   </script>
</layout:head>

<layout:body thisPage="administration_reports">
    <input type="hidden" name="reportId" id="reportId" />
    <input type="hidden" name="userId" id="userId"   />
    <input type="hidden" name="recordIds" id="recordIds" />
    <input type="hidden" name="authorName" id="authorName" />
    
    <div class="box">
        <div id="reportSelection" class="center">
            <h1><i18n:getText module="biblivre3" textKey="LABEL_MAINTENANCE_REPORTS2" /></h1>
            <div class="spacer2"></div>
            <select name="report" id="reportSelect" onchange="toggleDivs()" >
                <option value=""><i18n:getText module="biblivre3" textKey="LABEL_SELECT"/></option>
                <optgroup label="<i18n:getText module="biblivre3" textKey="MENU_ACQUISITION"/>">
                    <option value="1"><i18n:getText module="biblivre3" textKey="LINK_MAINTENANCE_REPORTS"/></option>
                </optgroup>
                <optgroup label="<i18n:getText module="biblivre3" textKey="MENU_CATALOGING"/>">
                    <option value="2"><i18n:getText module="biblivre3" textKey="LINK_MAINTENANCE_REPORTS2"/></option>
                    <option value="3"><i18n:getText module="biblivre3" textKey="LINK_MAINTENANCE_REPORTS3"/></option>
                    <option value="4"><i18n:getText module="biblivre3" textKey="LINK_MAINTENANCE_REPORTS4"/></option>
                    <option value="5"><i18n:getText module="biblivre3" textKey="LINK_MAINTENANCE_REPORTS5"/></option>
                    <option value="13"><i18n:getText module="biblivre3" textKey="LINK_MAINTENANCE_REPORTS13"/></option>
                    <option value="14"><i18n:getText module="biblivre3" textKey="LINK_MAINTENANCE_REPORTS14"/></option>
                    <option value="15"><i18n:getText module="biblivre3" textKey="LINK_MAINTENANCE_REPORTS15"/></option>
                    <option value="16"><i18n:getText module="biblivre3" textKey="LINK_MAINTENANCE_REPORTS16"/></option>
                </optgroup>
                <optgroup label="<i18n:getText module="biblivre3" textKey="MENU_CIRCULATION"/>">
                    <option value="6"><i18n:getText module="biblivre3" textKey="LINK_MAINTENANCE_REPORTS6"/></option>
                    <option value="7"><i18n:getText module="biblivre3" textKey="LINK_MAINTENANCE_REPORTS7"/></option>
                    <option value="8"><i18n:getText module="biblivre3" textKey="LINK_MAINTENANCE_REPORTS8"/></option>
                    <option value="12"><i18n:getText module="biblivre3" textKey="LINK_MAINTENANCE_REPORTS12"/></option>
                    <option value="9"><i18n:getText module="biblivre3" textKey="LINK_MAINTENANCE_REPORTS9"/></option>
                    <option value="10"><i18n:getText module="biblivre3" textKey="LINK_MAINTENANCE_REPORTS10"/></option>
                </optgroup>
            </select>
        </div>

        <div id="databaseSelection" class="hiddenReports center">
            <div class="spacer2"></div>

            <h1><i18n:getText module="biblivre3" textKey="LABEL_MAINTENANCE_REPORTS1" /></h1>

            <div class="spacer2"></div>

            <select name="database">
                <option value="<%=Database.MAIN%>"><i18n:getText module="biblivre3" textKey="VALUE_MAIN"/></option>
                <option value="<%=Database.WORK%>"><i18n:getText module="biblivre3" textKey="LABEL_WORK"/></option>
            </select>
        </div>

        <div id="orderDiv" class="hiddenReports center" >
            <div class="spacer2"></div>

            <h1><i18n:getText module="biblivre3" textKey="LABEL_ORDER_BY" /></h1>

            <div class="spacer2"></div>

            <select name="order">
                <option value="1"><i18n:getText module="biblivre3" textKey="VALUE_CLASSIFICATION"/></option>
                <option value="2"><i18n:getText module="biblivre3" textKey="VALUE_TITLE"/></option>
                <option value="3"><i18n:getText module="biblivre3" textKey="VALUE_AUTHOR"/></option>
            </select>
        </div>
            
        <div id="deweyDiv" class="hiddenReports center" >
            <div class="spacer2"></div>

            <h1><i18n:getText module="biblivre3" textKey="LABEL_DATAFIELD" /></h1>

            <div class="spacer2"></div>

            <select name="datafield">
                <option value="082">082 |a (<i18n:getText module="biblivre3" textKey="082"/>)</option>
                <option value="090">090 |a (<i18n:getText module="biblivre3" textKey="090"/>)</option>
            </select>
            
            <div class="spacer2"></div>

            <h1><i18n:getText module="biblivre3" textKey="LABEL_DEWEY_SIGNIFICANT_DIGITS" /></h1>

            <div class="spacer2"></div>

            <select name="digits">
                <option value="-1"><i18n:getText module="biblivre3" textKey="VALUE_ALL"/></option>
                <option value="1">1 (<i18n:getText module="biblivre3" textKey="LABEL_EG"/> 500)</option>
                <option value="2">2 (<i18n:getText module="biblivre3" textKey="LABEL_EG"/> 560)</option>
                <option value="3">3 (<i18n:getText module="biblivre3" textKey="LABEL_EG"/> 561)</option>
                <option value="4" selected="selected">4 (<i18n:getText module="biblivre3" textKey="LABEL_EG"/> 561.1)</option>
                <option value="5">5 (<i18n:getText module="biblivre3" textKey="LABEL_EG"/> 561.11)</option>
                <option value="6">6 (<i18n:getText module="biblivre3" textKey="LABEL_EG"/> 561.117)</option>
            </select>

        </div>            

        <div id="dateDiv" class="hiddenReports">
            <div class="spacer2"></div>

            <div class="format_form">
                <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_DATE_START"/></div>
                <div class="fcenter"><input type="text" name="initialDate" maxlength="10" class="finput" style="width:90px;" onkeypress="return Core.dateMask(this, event);"/>&nbsp;<i18n:getText module="biblivre3" textKey="DEFAULT_DATE_SCREEN"/></div>
                <div class="clear"></div>
            </div>
            <div class="format_form">
                <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_DATE_END"/></div>
                <div class="fcenter"><input type="text" name="finalDate" maxlength="10" class="finput" style="width:90px;" onkeypress="return Core.dateMask(this, event);"/>&nbsp;<i18n:getText module="biblivre3" textKey="DEFAULT_DATE_SCREEN"/></div>
                <div class="clear"></div>
            </div>
        </div>

        <div id="userDiv" class="hiddenReports">
            <div class="spacer2"></div>

            <h1><i18n:getText module="biblivre3" textKey="LABEL_SELECT_USER" /></h1>

            <div class="spacer2"></div>

            <div id="user_search_box" class="search_box">
                <table onkeypress="Core.submitKeyPress(event, Circulation.search);">
                    <tr class="search_term">
                        <td class="label_1_user"><i18n:getText module="biblivre3" textKey="LABEL_NAME" /> </td>
                        <td class="label_2_user"><input type="text" class="input_text input_term" name="SEARCH_NAME"/></td>
                        <td class="label_3_user">&#160;</td>
                    </tr>
                    <tr class="search_term_b">
                        <td class="label_1_user"><i18n:getText module="biblivre3" textKey="LABEL_ENROL" /> </td>
                        <td class="label_2_user"><input type="text" class="input_text input_user_enrol" name="SEARCH_USER_ID"/></td>
                        <td class="label_3_user">&#160;</td>
                    </tr>
                    <tr class="form_buttons">
                        <td colspan="3">
                            <div class="search_submit">
                                <button type="button" onclick="Circulation.search();"><i18n:getText module="biblivre3" textKey="BUTTON_SEARCH" /></button>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>

            <div class="spacer2"></div>

            <div class="paging user_search_paging"></div>

            <div id="user_search_results" class="box_holder">
                <div class="template pointer">
                    <div class="box" onclick="createUserReport('{userid}', '{name}');">
                        <div class="box_content">
                            <b><i18n:getText module="biblivre3" textKey="LABEL_NAME" /></b>: {name}<br/>
                            <b><i18n:getText module="biblivre3" textKey="LABEL_ENROL" /></b>: {userid}<br/>
                            {{login}<b><i18n:getText module="biblivre3" textKey="LABEL_USERNAME" /></b>: %%<br/>}
                        </div>
                    </div>
                </div>
            </div>

            <div class="paging user_search_paging"></div>
        </div>

        <div id="authorDiv" class="hiddenReports">
            <div class="spacer2"></div>

            <h1><i18n:getText module="biblivre3" textKey="LABEL_SELECT_AUTHOR" /></h1>

            <div class="spacer2"></div>

            <div id="author_search_box" class="search_box">
                <table onkeypress="Core.submitKeyPress(event, Reports.authorSearch);">
                    <tr class="search_term">
                        <td class="label_1_user"><i18n:getText module="biblivre3" textKey="LABEL_NAME" /> </td>
                        <td class="label_2_user"><input type="text" class="input_text input_term" name="SEARCH_NAME"/></td>
                        <td class="label_3_user">&#160;</td>
                    </tr>
                    <tr class="form_buttons">
                        <td colspan="3">
                            <div class="search_submit">
                                <button type="button" onclick="Reports.authorSearch();"><i18n:getText module="biblivre3" textKey="BUTTON_SEARCH" /></button>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>

            <div class="spacer2"></div>

            <div class="paging author_search_paging"></div>

            <div id="author_search_results" class="box_holder">
                <div class="template pointer">
                    <div class="box" onclick="createAuthorReport('{resultRecordIds}', '{name}');">
                        <div class="box_content">
                            <b><i18n:getText module="biblivre3" textKey="LABEL_NAME" /></b>: {name}<br/>
                            <b><i18n:getText module="biblivre3" textKey="LABEL_QUANTITY" /></b>: {size}<br/>
                        </div>
                    </div>
                </div>
            </div>

            <div class="paging author_search_paging"></div>
        </div>

        <div id="buttonDiv" class="hiddenReports center">
            <div class="spacer2"></div>
            <div class="spacer2"></div>
            <button type="button" style="width: 130px;" onclick="submitForm('FORM_1', 'GENERATE_REPORT');"><i18n:getText module="biblivre3" textKey="BUTTON_REPORT" /></button>
        </div>
    </div>
</layout:body>
