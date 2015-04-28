<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<i18n:setInitialLocale />

<layout:head>
    <link rel="stylesheet" type="text/css" href="css/cataloging.css" />
    <link rel="stylesheet" media="print" type="text/css" href="css/print.css" />

    <script type="text/javascript" src="scripts/acquisition.js"></script>
</layout:head>

<layout:body thisPage="acquisition_requisition">
    <div class="spacer"></div>

    <div class="tab tab_selected" rel="search" onclick="Core.changeTab('search', Acquisition.tabHandler, ['request']);">
        <i18n:getText module="biblivre3" textKey="TAB_ACQUISITION_SEARCH"/>
    </div>
    <div class="tab" rel="form" onclick="Core.changeTab('form', Acquisition.tabHandler, ['request']);">
        <i18n:getText module="biblivre3" textKey="TAB_ACQUISITION_FORM"/>
    </div>
    <div class="tab_close"></div>

    <div class="tab_body" rel="search">
        <div id="search_box" class="search_box">
            <table onkeypress="Core.submitKeyPress(event, Acquisition.searchSubmit, ['request']);">
                <tr>
                    <td class="label_1_acquisition"><i18n:getText module="biblivre3" textKey="LABEL_SEARCH_CONTAINING_TERM" /></td>
                    <td class="label_2_acquisition"><input type="text" class="input_text input_term" name="search_term"/></td>
                    <td class="label_3_acquisition">
                        <i18n:getText module="biblivre3" textKey="LABEL_SEARCH_ON_ATTRIBUTE" />
                        <select name="search_type">
                            <option value="serial"><i18n:getText module="biblivre3" textKey="LABEL_REQUISITION_NUMBER" /></option>
                            <option value="requester"><i18n:getText module="biblivre3" textKey="LABEL_PETITIONER" /></option>
                            <option value="requestDate"><i18n:getText module="biblivre3" textKey="LABEL_DATE" /></option>
                            <option value="author"><i18n:getText module="biblivre3" textKey="LABEL_AUTHOR" /></option>
                            <option value="title"><i18n:getText module="biblivre3" textKey="LABEL_TITLE" /></option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td colspan="3" class="center">
                        <div class="spacer2"></div>
                        <input name="statusCheckBox" id="statusCheckBox1" type="radio" value="0" checked="checked"/><label for="statusCheckBox1"><i18n:getText module="biblivre3" textKey="LABEL_RADIO_OPEN" /></label>
                        &#160;&#160;
                        <input name="statusCheckBox" id="statusCheckBox2" type="radio" value="1" /><label for="statusCheckBox2"><i18n:getText module="biblivre3" textKey="LABEL_RADIO_ATTENDED" /></label>
                        &#160;&#160;
                        <input name="statusCheckBox" id="statusCheckBox3" type="radio" value="2" /><label for="statusCheckBox3"><i18n:getText module="biblivre3" textKey="LABEL_RADIO_BOTH" /></label>
                    </td>
                </tr>
                <tr class="form_buttons">
                    <td colspan="3">
                        <div class="search_list_all">
                            <button type="button" onclick="Acquisition.searchSubmit('request', true);"><i18n:getText module="biblivre3" textKey="BUTTON_LIST_ALL" /></button>
                        </div>
                        <div class="search_submit">
                            <button type="button" onclick="Acquisition.searchSubmit('request');"><i18n:getText module="biblivre3" textKey="BUTTON_SEARCH" /></button>
                        </div>
                        <div class="search_add">
                            <button type="button" onclick="Acquisition.createNew('request');"><i18n:getText module="biblivre3" textKey="BUTTON_NEW" /></button>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
        <div class="clear"></div>
    </div>

    <div id="form_edit_box" class="tab_body hidden" rel="form">
        <div style="font-size: 12px;">
            <fieldset class="datafield">
                <legend><i18n:getText module="biblivre3" textKey="LABEL_GENERAL_DATA"/></legend>

                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_DATE_REQUISITION"/></div>
                    <div class="fcenter"><input type="text" name="requestDate" maxlength="10" class="finput" disabled="true"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_RESPONSIBLE"/></div>
                    <div class="fcenter"><input type="text" name="user" maxlength="30" class="finput use_default_value" disabled="true" value="${LOGGED_USER.loginName}" defaultvalue="${LOGGED_USER.loginName}" /></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_APPLICANT"/></div>
                    <div class="fcenter"><input type="text" name="requester" maxlength="30" class="finput" /></div>
                    <div class="clear"></div>
                </div>

                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_AMOUNT_HOLDING"/></div>
                    <div class="fcenter"><input type="text" name="quantity" maxlength="6" class="finput"/></div>
                    <div class="clear"></div>
                </div>
            </fieldset>
                    
            <fieldset class="datafield">
                <legend><i18n:getText module="biblivre3" textKey="LABEL_MATERIAL_INFORMATION"/></legend>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_TYPE_AUTHOR"/></div>
                    <div class="fcenter">
                        <select name="authorType">
                            <option value="100"><i18n:getText module="biblivre3" textKey="VALUE_SELECT_PERSON"/></option>
                            <option value="111"><i18n:getText module="biblivre3" textKey="VALUE_SELECT_EVENT"/></option>
                            <option value="110"><i18n:getText module="biblivre3" textKey="VALUE_SELECT_COLLECTIVE_ENTITY"/></option>
                        </select>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_NAME"/></div>
                    <div class="fcenter"><input type="text" name="author" maxlength="50" class="finput" /></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_AUTHOR_NUMERATION"/></div>
                    <div class="fcenter"><input type="text" name="authorNumeration" maxlength="20" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_AUTHOR_TITLE"/></div>
                    <div class="fcenter"><input type="text" name="authorTitle" maxlength="50" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_TITLE_MAIN_TITLE"/></div>
                    <div class="fcenter"><input type="text" name="title" maxlength="100" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_TITLE_OTHER_TITLES_SUBTITLES"/></div>
                    <div class="fcenter"><input type="text" name="subtitle" maxlength="100" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_NUMBER_EDITION"/></div>
                    <div class="fcenter"><input type="text" name="editionNumber" maxlength="20" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_PUBLISHER"/></div>
                    <div class="fcenter"><input type="text" name="publisher" maxlength="50" class="finput"/></div>
                    <div class="clear"></div>
                </div>
            </fieldset>

            <fieldset>
                <legend><i18n:getText module="biblivre3" textKey="LABEL_OBSERVATION"/></legend>
                <textarea name="obs" style="width: 99%;" rows="4"></textarea>
            </fieldset>
        </div>

        <div class="spacer"></div>
        <div class="submit_buttons">
            <button type="button" onclick="Core.changeTab('search', Acquisition.tabHandler, ['request']);"><i18n:getText module="biblivre3" textKey="BUTTON_CANCEL" /></button>
            <button type="button" onclick="Core.tempDisable(this); Acquisition.save('request');"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE" /></button>
            <div class="clear"></div>
        </div>
    </div>

    <div class="tab_footer" rel="search">
        <div class="paging search_paging"></div>

        <div class="search_print" style="text-align: center; margin-bottom: 10px;"><button type="button" onclick="window.print();"><i18n:getText module="biblivre3" textKey="BUTTON_PRINT" /></button></div>

        <div id="search_results" class="box_holder">
            <div class="template pointer">
                <div class="box" rel="{serial}" onclick="Core.changeTab('form', Acquisition.tabHandler, ['request', '{serial}']);">
                    <div class="box_content_left">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_SERIAL" />:</b> {serial}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_AUTHOR" /></b> {author}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_TITLE" />:</b> {title}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_CREATION_DATE" />:</b> {created|Date}<br/>
                    </div>
                    <div class="box_content_right">
                        <div><button type="button" onclick="Core.runStopingEvent(event, Acquisition.deleteRecord, ['{serial}', this, 'request']);"><i18n:getText module="biblivre3" textKey="BUTTON_DELETE" /></button></div>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>

        <div class="paging search_paging"></div>
    </div>

</layout:body>