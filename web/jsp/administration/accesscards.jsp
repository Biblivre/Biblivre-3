<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<i18n:setInitialLocale />

<layout:head>
    <link rel="stylesheet" type="text/css" href="css/access.css" />

    <script type="text/javascript" src="scripts/access.js"></script>
</layout:head>

<layout:body thisPage="administration_accesscards">
    <div class="spacer"></div>

    <div class="tab tab_selected" rel="search" onclick="Core.changeTab('search', Access.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_ACCESSADMIN_SEARCH"/>
    </div>
    <div class="tab" rel="form" onclick="Core.changeTab('form', Access.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_ACCESSADMIN_ADD_CARDS"/>
    </div>
    <div class="tab_close"></div>

    <div class="tab_body" rel="search">
        <div id="card_search_box" class="search_box">
            <table onkeypress="Core.submitKeyPress(event, Access.searchSubmit);">
                <tr class="search_term">
                    <td class="label_1"><i18n:getText module="biblivre3" textKey="LABEL_SEARCH_CONTAINING_TERM" /></td>
                    <td class="label_2"><input type="text" class="input_text input_term" name="SEARCH_TERM"/></td>
                    <td class="label_3">&#160;</td>
                </tr>
                <tr class="form_buttons">
                    <td colspan="3">
                        <div class="search_list_all">
                            <button type="button" onclick="Access.searchSubmit(true);"><i18n:getText module="biblivre3" textKey="BUTTON_LIST_ALL" /></button>
                        </div>
                        <div class="search_submit">
                            <button type="button" onclick="Access.searchSubmit();"><i18n:getText module="biblivre3" textKey="BUTTON_SEARCH" /></button>
                        </div>
                        <div class="search_add">
                            <button type="button" onclick="Access.createNew();"><i18n:getText module="biblivre3" textKey="BUTTON_NEW" /></button>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    </div>

    <div class="tab_body hidden" rel="form">
        <fieldset id="single_card">
            <legend><i18n:getText module="biblivre3" textKey="LABEL_REGISTER_CARDS"/></legend>
            <div class="spacer"></div>

            <div class="center">
                <input size="20" maxlength="10" type="text" name="newCard" />
                <div class="spacer2"></div>
                <button type="button" onclick="Access.save('single_card');"><i18n:getText module="biblivre3" textKey="LABEL_REGISTER_CARDS" /></button>
            </div>

            <div class="spacer2"></div>
        </fieldset>

        <div class="spacer2"></div>

        <fieldset id="card_list">
            <legend><i18n:getText module="biblivre3" textKey="LABEL_REGISTER_CARDS_SEQUENCE"/></legend>

            <div class="help_message">
                <i18n:getText module="biblivre3" textKey="LABEL_ADMINISTRATION_CARD1"/>
                <i18n:getText module="biblivre3" textKey="LABEL_ADMINISTRATION_CARD2"/>
                <i18n:getText module="biblivre3" textKey="LABEL_ADMINISTRATION_CARD3"/>
            </div>
            
            <div style="font-size:12px;" class="center">
                <div class="format_form" >
                    <div class="fleft"><b><i18n:getText module="biblivre3" textKey="LABEL_PREFIX"/>:</b></div>
                    <div class="fcenter" style="width:150px;"><input type="text" class="finput" name="prefix" id="prefix" maxlength="10" onkeyup="Access.previewForm();" /></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><b><i18n:getText module="biblivre3" textKey="LABEL_SUFFIX"/>:</b></div>
                    <div class="fcenter" style="width:150px;"><input type="text" class="finput" name="suffix" id="suffix" maxlength="10" onkeyup="Access.previewForm();" /></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><b><i18n:getText module="biblivre3" textKey="LABEL_NUMBER_START"/>:</b></div>
                    <div class="fcenter" style="width:150px;"><input type="text" class="finput" name="start" id="start" maxlength="10" onkeyup="Access.previewForm();" /></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><b><i18n:getText module="biblivre3" textKey="LABEL_NUMBER_END"/>:</b></div>
                    <div class="fcenter" style="width:150px;"><input type="text" class="finput" name="end" id="end" maxlength="10" onkeyup="Access.previewForm();" /></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><b><i18n:getText module="biblivre3" textKey="LABEL_PRE_VISUALIZATION"/>:</b></div>
                    <div class="fcenter" style="width:150px;"><input type="text" class="finput" name="preview" id="preview" maxlength="10" disabled="disabled"/></div>
                    <div class="clear"></div>
                </div>
                <div class="spacer2"></div>
                <button  type="button" onclick="Access.save('card_list');"><i18n:getText module="biblivre3" textKey="LABEL_REGISTER_CARDS_SEQUENCE" /></button>
            </div>
            <div class="spacer2"></div>
        </fieldset>

    </div>

    <div class="tab_footer" rel="search">
        <div class="paging search_paging"></div>

        <div class="search_print" style="text-align: center; margin-bottom: 10px;"><button type="button" onclick="window.print();"><i18n:getText module="biblivre3" textKey="BUTTON_PRINT" /></button></div>

        <div id="search_results" class="box_holder">
            <div class="template">
                <div class="box box card_status_{status}">
                    <div class="box_content_left">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_NAME" />:</b> {cardNumber}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_SERIAL" />:</b> {serialCard}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_SITUATION" /></b>: {status|translation|CARD_STATUS}<br/>
                    </div>
                    <div class="box_content_right">
                        <div><button type="button" onclick="Core.runStopingEvent(event, Access.deleteCard, ['{cardNumber}', '{serialCard}', this]);"><i18n:getText module="biblivre3" textKey="BUTTON_DELETE" /></button></div>
                        <div><button type="button" class="block_card" onclick="Core.runStopingEvent(event, Access.blockCard, ['{serialCard}', this]);"><i18n:getText module="biblivre3" textKey="BUTTON_BLOCK_USER" /></button></div>
                        <div><button type="button" class="unblock_card" onclick="Core.runStopingEvent(event, Access.unBlockCard, ['{serialCard}', this]);"><i18n:getText module="biblivre3" textKey="BUTTON_UNBLOCK_USER" /></button></div>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>

        <div class="paging search_paging"></div>
    </div>
</layout:body>





