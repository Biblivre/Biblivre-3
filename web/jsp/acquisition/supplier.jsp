<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<i18n:setInitialLocale />

<layout:head>
    <link rel="stylesheet" type="text/css" href="css/cataloging.css" />
    <link rel="stylesheet" media="print" type="text/css" href="css/print.css" />

    <script type="text/javascript" src="scripts/acquisition.js"></script>
</layout:head>

<layout:body thisPage="acquisition_supplier">
    <div class="spacer"></div>

    <div class="tab tab_selected" rel="search" onclick="Core.changeTab('search', Acquisition.tabHandler, ['supplier']);">
        <i18n:getText module="biblivre3" textKey="TAB_ACQUISITION_SEARCH"/>
    </div>
    <div class="tab" rel="form" onclick="Core.changeTab('form', Acquisition.tabHandler, ['supplier']);">
        <i18n:getText module="biblivre3" textKey="TAB_ACQUISITION_FORM"/>
    </div>
    <div class="tab_close"></div>

    <div class="tab_body" rel="search">
        <div id="search_box" class="search_box">
            <table onkeypress="Core.submitKeyPress(event, Acquisition.searchSubmit, ['supplier']);">
                <tr>
                    <td class="label_1_acquisition"><i18n:getText module="biblivre3" textKey="LABEL_SEARCH_CONTAINING_TERM" /></td>
                    <td class="label_2_acquisition"><input type="text" class="input_text input_term" name="search_term"/></td>
                    <td class="label_3_acquisition">
                        <i18n:getText module="biblivre3" textKey="LABEL_SEARCH_ON_ATTRIBUTE" />
                        <select name="search_type">
                            <option value="trademarkName"><i18n:getText module="biblivre3" textKey="LABEL_TRADING_NAME"/></option>
                            <option value="companyName"><i18n:getText module="biblivre3" textKey="LABEL_COMPANY_NAME"/></option>
                            <option value="companyNumber"><i18n:getText module="biblivre3" textKey="LABEL_COMPANY_NUMBER"/></option>
                        </select>
                    </td>
                </tr>
                <tr class="form_buttons">
                    <td colspan="3">
                        <div class="search_list_all">
                            <button type="button" onclick="Acquisition.searchSubmit('supplier', true);"><i18n:getText module="biblivre3" textKey="BUTTON_LIST_ALL" /></button>
                        </div>
                        <div class="search_submit">
                            <button type="button" onclick="Acquisition.searchSubmit('supplier');"><i18n:getText module="biblivre3" textKey="BUTTON_SEARCH" /></button>
                        </div>
                        <div class="search_add">
                            <button type="button" onclick="Acquisition.createNew('supplier');"><i18n:getText module="biblivre3" textKey="BUTTON_NEW" /></button>
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
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_DATE_REQUISITION"/></div>
                    <div class="fcenter"><input type="text" name="created" data="Date" maxlength="256" class="finput" disabled="true"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_LAST_UPDATE"/></div>
                    <div class="fcenter"><input type="text" name="lastUpdate" data="Date" maxlength="256" class="finput" disabled="true"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_TRADING_NAME"/></div>
                    <div class="fcenter"><input type="text" name="trademarkName" maxlength="100" class="finput" /></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_COMPANY_NAME"/></div>
                    <div class="fcenter"><input type="text" name="companyName" maxlength="100" class="finput" /></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_COMPANY_NUMBER"/></div>
                    <div class="fcenter"><input type="text" name="companyNumber" maxlength="18" class="finput" /></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_VAT_REGISTRATION_NUMBER"/></div>
                    <div class="fcenter"><input type="text" name="vatRegistrationNumber" maxlength="20" class="finput" /></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_ADDRESS"/></div>
                    <div class="fcenter"><input type="text" name="address" maxlength="70" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_NUMBER"/></div>
                    <div class="fcenter"><input type="text" name="addressNumber" maxlength="10" class="finput" style="width:90px;"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_COMPLEMENT"/></div>
                    <div class="fcenter"><input type="text" name="complement" maxlength="50" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_NEIGHBORHOOD"/></div>
                    <div class="fcenter"><input type="text" name="area" maxlength="30" class="finput" /></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_CITY"/></div>
                    <div class="fcenter"><input type="text" name="city" maxlength="30" class="finput" /></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_STATE"/></div>
                    <div class="fcenter">
                        <select name="state">
                            <option value="AC">Acre</option>
                            <option value="AL">Alagoas</option>
                            <option value="AP">Amapá</option>
                            <option value="AM">Amazonas</option>
                            <option value="BA">Bahia</option>
                            <option value="CE">Ceará</option>
                            <option value="DF">Distrito Federal</option>
                            <option value="ES">Espírito Santo</option>
                            <option value="GO">Goiás</option>
                            <option value="MA">Maranhão</option>
                            <option value="MT">Mato Grosso</option>
                            <option value="MS">Mato Grosso do Sul</option>
                            <option value="MG">Minas Gerais</option>
                            <option value="PA">Pará</option>
                            <option value="PB">Paraíba</option>
                            <option value="PR">Paraná</option>
                            <option value="PE">Pernambuco</option>
                            <option value="PI">Piauí</option>
                            <option value="RJ">Rio de Janeiro</option>
                            <option value="RN">Rio Grande do Norte</option>
                            <option value="RS">Rio Grande do Sul</option>
                            <option value="RO">Rondônia</option>
                            <option value="RR">Roraima</option>
                            <option value="SC">Santa Catarina</option>
                            <option value="SP">São Paulo</option>
                            <option value="SE">Sergipe</option>
                            <option value="TO">Tocantins</option>
                    </select></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_COUNTRY"/></div>
                    <div class="fcenter"><input type="text" name="country" maxlength="30" class="finput" /></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_ZIP_CODE"/></div>
                    <div class="fcenter"><input type="text" name="zipCode" maxlength="10" class="finput" style="width:90px;"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_URL"/></div>
                    <div class="fcenter"><input type="text" name="url" maxlength="150" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_EMAIL"/></div>
                    <div class="fcenter"><input type="text" name="email" maxlength="100" class="finput"/></div>
                    <div class="clear"></div>
                </div>
            </fieldset>

            <fieldset>
                <legend><i18n:getText module="biblivre3" textKey="LABEL_TELEPHONE_CONTACT"/></legend>
                <div class="format_form">
                    <div class="fleft"><input type="text" name="contact1" maxlength="30" class="finput" style="width:150px;" /></div>
                    <div class="fcenter"><input type="text" name="telephone1" maxlength="20" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><input type="text" name="contact2" maxlength="30" class="finput" style="width:150px;" /></div>
                    <div class="fcenter"><input type="text" name="telephone2" maxlength="20" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><input type="text" name="contact3" maxlength="30" class="finput" style="width:150px;" /></div>
                    <div class="fcenter"><input type="text" name="telephone3" maxlength="20" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="format_form">
                    <div class="fleft"><input type="text" name="contact4" maxlength="30" class="finput" style="width:150px;" /></div>
                    <div class="fcenter"><input type="text" name="telephone4" maxlength="20" class="finput"/></div>
                    <div class="clear"></div>
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
            <button type="button" onclick="Core.changeTab('search', Acquisition.tabHandler, ['supplier']);"><i18n:getText module="biblivre3" textKey="BUTTON_CANCEL" /></button>
            <button type="button" onclick="Core.tempDisable(this); Acquisition.save('supplier');"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE" /></button>
            <div class="clear"></div>
        </div>
    </div>

    <div class="tab_footer" rel="search">
        <div class="paging search_paging"></div>

        <div class="search_print" style="text-align: center; margin-bottom: 10px;"><button type="button" onclick="window.print();"><i18n:getText module="biblivre3" textKey="BUTTON_PRINT" /></button></div>

        <div id="search_results" class="box_holder">
            <div class="template pointer">
                <div class="box" rel="{serial}" onclick="Core.changeTab('form', Acquisition.tabHandler, ['supplier', '{serial}']);">
                    <div class="box_content_left">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_NAME" /></b> {name}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_SERIAL" />:</b> {serial}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_CREATION_DATE" />:</b> {created|Date}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_LAST_UPDATE" />:</b> {modified|Date}<br/>
                    </div>
                    <div class="box_content_right">
                        <div><button type="button" onclick="Core.runStopingEvent(event, Acquisition.deleteRecord, ['{serial}', this, 'supplier']);"><i18n:getText module="biblivre3" textKey="BUTTON_DELETE" /></button></div>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>

        <div class="paging search_paging"></div>
    </div>

</layout:body>