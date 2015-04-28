<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<i18n:setInitialLocale />

<layout:head>
    <link rel="stylesheet" type="text/css" href="css/acquisition.css" />
    <link rel="stylesheet" media="print" type="text/css" href="css/print.css" />

    <script type="text/javascript" src="scripts/acquisition.js"></script>
</layout:head>

<layout:body thisPage="acquisition_quotation">
    <div class="spacer"></div>
    <div class="tab tab_selected" rel="search" onclick="Core.changeTab('search', Acquisition.tabHandler, ['quotation']);">
        <i18n:getText module="biblivre3" textKey="TAB_ACQUISITION_SEARCH"/>
    </div>
    <div class="tab" rel="form" onclick="Core.changeTab('form', Acquisition.tabHandler, ['quotation']);">
        <i18n:getText module="biblivre3" textKey="TAB_ACQUISITION_FORM"/>
    </div>
    <div class="tab_close"></div>

    <div class="tab_body" rel="search">
        <div id="search_box" class="search_box">
            <table onkeypress="Core.submitKeyPress(event, Acquisition.searchSubmit, ['quotation']);">
                <tr>
                    <td class="label_1_acquisition"><i18n:getText module="biblivre3" textKey="LABEL_SEARCH_CONTAINING_TERM" /></td>
                    <td class="label_2_acquisition"><input type="text" class="input_text input_term" name="search_term"/></td>
                    <td class="label_3_acquisition">
                        <i18n:getText module="biblivre3" textKey="LABEL_SEARCH_ON_ATTRIBUTE" />
                        <select name="search_type">
                            <option value="supplierName"><i18n:getText module="biblivre3" textKey="LABEL_SUPPLIER" /></option>
                            <option value="quotationDate"><i18n:getText module="biblivre3" textKey="LABEL_DATE" /></option>
                            <option value="serial"><i18n:getText module="biblivre3" textKey="LABEL_QUOTATION_NUMBER" /></option>
                        </select>
                    </td>
                </tr>
                <tr class="form_buttons">
                    <td colspan="3">
                        <div class="search_list_all">
                            <button type="button" onclick="Acquisition.searchSubmit('quotation', true);"><i18n:getText module="biblivre3" textKey="BUTTON_LIST_ALL" /></button>
                        </div>
                        <div class="search_submit">
                            <button type="button" onclick="Acquisition.searchSubmit('quotation');"><i18n:getText module="biblivre3" textKey="BUTTON_SEARCH" /></button>
                        </div>
                        <div class="search_add">
                            <button type="button" onclick="Acquisition.createNew('quotation');"><i18n:getText module="biblivre3" textKey="BUTTON_NEW" /></button>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
        <div class="clear"></div>
    </div>

    <div id="form_edit_box" class="tab_body hidden" rel="form">
        <div style="font-size: 12px;">
            <fieldset>
                <legend><i18n:getText module="biblivre3" textKey="LABEL_GENERAL_DATA"/></legend>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_SUPPLIER"/></div>
                    <div class="fcenter">
                        <select name="serialSupplier">
                            <option value=""><i18n:getText module="biblivre3" textKey="LABEL_SELECT_SUPPLIER" /></option>
                            <c:forEach items="${supplierList}" var="supplier">
                                <option value="${supplier.serial}">${supplier.trademarkName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_DATE_ORDER_QUOTATION"/></div>
                    <div class="fcenter"><input type="text" name="quotationDate" data="Date" maxlength="10" class="finput" style="width:90px;" onkeypress="return Core.dateMask(this, event);"/>&nbsp;<i18n:getText module="biblivre3" textKey="DEFAULT_DATE_SCREEN"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_DATE_ARRIVAL_QUOTATION"/></div>
                    <div class="fcenter"><input type="text" name="responseDate" data="Date" maxlength="10" class="finput" style="width:90px;" onkeypress="return Core.dateMask(this, event);"/>&nbsp;<i18n:getText module="biblivre3" textKey="DEFAULT_DATE_SCREEN"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_DATE_EXPIRATION_QUOTATION"/></div>
                    <div class="fcenter"><input type="text" name="expirationDate" data="Date" maxlength="10" class="finput" style="width:90px;" onkeypress="return Core.dateMask(this, event);"/>&nbsp;<i18n:getText module="biblivre3" textKey="DEFAULT_DATE_SCREEN"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_DELIVERY_TIME"/></div>
                    <div class="fcenter"><input type="text" name="deliveryTime" maxlength="256" class="finput" style="width:90px;"/></div>
                    <div class="clear"></div>
                </div>
            </fieldset>
            <fieldset>
                <legend><i18n:getText module="biblivre3" textKey="LABEL_VALUES"/></legend>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_NUMBER_REQUISITION"/></div>
                    <div class="fcenter">
                        <select name="serialRequisition" onchange="AcquisitionQuotation.selectRequest(this);">
                            <option value=""><i18n:getText module="biblivre3" textKey="LABEL_SELECT_REQUISITION"/></option>
                            <c:forEach items="${requestList}" var="request">
                                <option value="${request.serial}|${request.quantity}">${request.serial} - ${request.title}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_QUANTITY"/></div>
                    <div class="fcenter"><input type="text" name="quotationQuantity" maxlength="256" class="finput" style="width:90px;"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_UNIT_VALUE"/></div>
                    <div class="fcenter"><input type="text" name="unitValue" maxlength="256" class="finput" style="width:90px;"/></div>
                    <div class="clear"></div>
                    <div class="spacer2"></div>
                    <div align="center">
                        <button type="button" style="width: 200px;" onclick="AcquisitionQuotation.addRequest(this);"><i18n:getText module="biblivre3" textKey="BUTTON_ADD"/></button>
                    </div>
                    <table width="100%" class="requisition_table">
                        <thead>
                            <tr>
                                <th><i18n:getText module="biblivre3" textKey="LABEL_REQUISITION"/></th>
                                <th style="width: 100px;"><i18n:getText module="biblivre3" textKey="LABEL_QUANTITY"/></th>
                                <th style="width: 120px;"><i18n:getText module="biblivre3" textKey="LABEL_UNIT_VALUE"/></th>
                                <th style="width: 60px;"><i18n:getText module="biblivre3" textKey="BUTTON_DELETE"/></th>
                            </tr>
                        </thead>
                        <tbody>

                        </tbody>
                    </table>
                </div>
            </fieldset>
            <fieldset>
                <legend><i18n:getText module="biblivre3" textKey="LABEL_OBSERVATION"/></legend>
                <div>
                    <div><textarea name="obs" style="width: 99%;" rows="4"></textarea></div>
                    <div class="clear"></div>
                </div>
            </fieldset>
        </div>

        <div class="spacer"></div>
        <div class="submit_buttons">
            <button type="button" onclick="Core.changeTab('search', Acquisition.tabHandler, ['quotation']);"><i18n:getText module="biblivre3" textKey="BUTTON_CANCEL" /></button>
            <button type="button" onclick="Core.tempDisable(this); Acquisition.save('quotation');"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE" /></button>
            <div class="clear"></div>
        </div>
    </div>

    <div class="tab_footer" rel="search">
        <div class="paging search_paging"></div>

        <div class="search_print" style="text-align: center; margin-bottom: 10px;"><button type="button" onclick="window.print();"><i18n:getText module="biblivre3" textKey="BUTTON_PRINT" /></button></div>

        <div id="search_results" class="box_holder">
            <div class="template pointer">
                <div class="box" rel="{serial}" onclick="Core.changeTab('form', Acquisition.tabHandler, ['quotation', '{serial}']);">
                    <div class="box_content_left">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_SERIAL" />:</b> {serial}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_SUPPLIER" />:</b> {supplierName}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_ITEMS" />:</b><br/><div style="margin-left:30px;">{items|StringList}</div>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_CREATION_DATE" />:</b> {quotationDate|Date}<br/>
                    </div>
                    <div class="box_content_right">
                        <div><button type="button" onclick="Core.runStopingEvent(event, Acquisition.deleteRecord, ['{serial}', this, 'quotation']);"><i18n:getText module="biblivre3" textKey="BUTTON_DELETE" /></button></div>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>

        <div class="paging search_paging"></div>
    </div>

</layout:body>