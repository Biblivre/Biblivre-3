<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<i18n:setInitialLocale />

<layout:head>
    <link rel="stylesheet" type="text/css" href="css/cataloging.css" />
    <link rel="stylesheet" media="print" type="text/css" href="css/print.css" />

    <script type="text/javascript" src="scripts/json.js"></script>
    <script type="text/javascript" src="scripts/cataloging.js"></script>
    <script type="text/javascript" src="scripts/authorities.js"></script>

    <script type="text/javascript">
        $(document).ready(function() {
            Cataloging.initMarcHelp('auth');
            Cataloging.initRepeatableDataFields();
            Cataloging.initRepeatableSubFields();

            Cataloging.toggleMarcNumbering({ checked: true });
        });
    </script>
</layout:head>

<layout:body thisPage="cataloging_auth">
    <div class="spacer"></div>

    <div class="tab tab_selected" rel="search" onclick="Core.changeTab('search', Authorities.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_SEARCH"/>
    </div>
    <div class="tab" rel="form" onclick="Core.changeTab('form', Authorities.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_FORM"/>
    </div>
    <div class="tab" rel="marc" onclick="Core.changeTab('marc', Authorities.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_MARC"/>
    </div>
    <div class="tab_close"></div>

    <div class="tab_body" rel="search">
        <div id="search_box" class="search_box">
            <table onkeypress="Core.submitKeyPress(event, Authorities.searchSubmit);">
                <tr class="search_term">
                    <td class="label_1"><i18n:getText module="biblivre3" textKey="LABEL_SEARCH_CONTAINING_TERM" /></td>
                    <td class="label_2"><input type="text" class="input_text input_term" name="SEARCH_TERM"/></td>
                    <td class="label_3">&#160;</td>
                </tr>
                <tr class="form_buttons">
                    <td colspan="3">
                        <div class="search_list_all">
                            <button type="button" onclick="Authorities.searchSubmit(true);"><i18n:getText module="biblivre3" textKey="BUTTON_LIST_ALL" /></button>
                        </div>
                        <div class="search_submit">
                            <button type="button" onclick="Authorities.searchSubmit();"><i18n:getText module="biblivre3" textKey="BUTTON_SEARCH" /></button>
                        </div>
                        <div class="search_add">
                            <button type="button" onclick="Authorities.createNew();"><i18n:getText module="biblivre3" textKey="BUTTON_NEW" /></button>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    </div>

    <div class="tab_body hidden" rel="form">
        <div class="navigation_top">
            <button class="navigation_button_left" type="button" onclick="Authorities.searchNavigate('form', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="Authorities.searchNavigate('form', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <h1 class="tab_inner_title"></h1>
            <div class="clear"></div>
        </div>

        <div class="toggles">
            <i18n:getText module="biblivre3" textKey="LABEL_SHOW_MARC_TAG_NUMBERS"/>
            <input type="checkbox" class="show_marc" onclick="Cataloging.toggleMarcNumbering(this);"/>
        </div>

        <div style="font-size:12px;">
            <div class="clear"></div>

            <fieldset class="noborder">
                <div>
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_AUTHOR"/></div>

                    <div class="fcenter">
                        <select name="author_type" onchange="Cataloging.toggleAreas(this.name, this.value);">
                            <option value=""><i18n:getText module="biblivre3" textKey="VALUE_SELECT_TYPE_AUTHOR"/></option>
                            <option value="100"><i18n:getText module="biblivre3" textKey="VALUE_SELECT_PERSON"/></option>
                            <option value="111"><i18n:getText module="biblivre3" textKey="VALUE_SELECT_EVENT"/></option>
                            <option value="110"><i18n:getText module="biblivre3" textKey="VALUE_SELECT_COLLECTIVE_ENTITY"/></option>
                        </select>
                    </div>

                    <div class="clear"></div>
                </div>
            </fieldset>

            <div class="author_type" data="100" style="display:none;">
                <fieldset class="datafield" data="100">
                    <legend><i18n:getText module="biblivre3" textKey="a_100"/></legend>

                    <div class="indicator">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_100_i1"/></div>

                        <div class="fcenter">
                            <select name="ind1">
                                <option value="0"><i18n:getText module="biblivre3" textKey="a_100_i1_0"/></option>
                                <option value="1"><i18n:getText module="biblivre3" textKey="a_100_i1_1"/></option>
                                <option value="2"><i18n:getText module="biblivre3" textKey="a_100_i1_2"/></option>
                                <option value="3"><i18n:getText module="biblivre3" textKey="a_100_i1_3"/></option>
                            </select>
                        </div>

                        <div class="clear"></div>
                    </div>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_100_a"/></div>
                        <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput" /></div>
                        <div class="clear"></div>
                    </div>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_100_b"/></div>
                        <div class="fcenter"><input type="text" name="b" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>

                    <div class="subfield repeatable">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_100_c"/></div>
                        <div class="fcenter"><input type="text" name="c" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_100_d"/></div>
                        <div class="fcenter"><input type="text" name="d" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_100_q"/></div>
                        <div class="fcenter"><input type="text" name="q" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>
                </fieldset>

                <fieldset class="datafield repeatable" data="400">
                    <legend><i18n:getText module="biblivre3" textKey="a_400"/></legend>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_400_a"/></div>
                        <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>
                </fieldset>

                <fieldset class="datafield repeatable" data="670">
                    <legend><i18n:getText module="biblivre3" textKey="a_670"/></legend>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_670_a"/></div>
                        <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_670_b"/></div>
                        <div class="fcenter"><textarea name="b" class="finput"></textarea></div>
                        <div class="clear"></div>
                    </div>
                </fieldset>
            </div>

            <div class="author_type" data="111" style="display: none;">
                <fieldset class="datafield" data="111">
                    <legend><i18n:getText module="biblivre3" textKey="a_111"/></legend>

                    <div class="indicator">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_111_i1"/></div>

                        <div class="fcenter">
                            <select name="ind1">
                                <option value="0"><i18n:getText module="biblivre3" textKey="a_111_i1_0"/></option>
                                <option value="1"><i18n:getText module="biblivre3" textKey="a_111_i1_1"/></option>
                                <option value="2"><i18n:getText module="biblivre3" textKey="a_111_i1_2"/></option>
                            </select>
                        </div>

                        <div class="clear"></div>
                    </div>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_111_a"/></div>
                        <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_111_c"/></div>
                        <div class="fcenter"><input type="text" name="c" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_111_d"/></div>
                        <div class="fcenter"><input type="text" name="d" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>

                    <div class="subfield repeatable">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_111_e"/></div>
                        <div class="fcenter"><input type="text" name="e" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_111_g"/></div>
                        <div class="fcenter"><input type="text" name="g" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_111_k"/></div>
                        <div class="fcenter"><input type="text" name="k" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>

                    <div class="subfield repeatable">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_111_n"/></div>
                        <div class="fcenter"><input type="text" name="n" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>
                </fieldset>

                <fieldset class="datafield repeatable" data="411">
                    <legend><i18n:getText module="biblivre3" textKey="a_411"/></legend>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_411_a"/></div>
                        <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>
                </fieldset>

                <fieldset class="datafield repeatable" data="670">
                    <legend><i18n:getText module="biblivre3" textKey="a_670"/></legend>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_670_a"/></div>
                        <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_670_b"/></div>
                        <div class="fcenter"><textarea name="b" class="finput"></textarea></div>
                        <div class="clear"></div>
                    </div>
                </fieldset>
            </div>

            <div class="author_type" data="110" style="display: none;">
                <fieldset class="datafield" data="110">
                    <legend><i18n:getText module="biblivre3" textKey="a_110"/></legend>

                    <div class="indicator">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_110_i1"/></div>

                        <div class="fcenter">
                                <select name="ind1">
                                    <option value="0"><i18n:getText module="biblivre3" textKey="a_110_i1_0"/></option>
                                    <option value="1"><i18n:getText module="biblivre3" textKey="a_110_i1_1"/></option>
                                    <option value="2"><i18n:getText module="biblivre3" textKey="a_110_i1_2"/></option>
                                </select>
                        </div>

                        <div class="clear"></div>
                    </div>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_110_a"/></div>
                        <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>

                    <div class="subfield repeatable">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_110_b"/></div>
                        <div class="fcenter"><input type="text" name="b" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_110_c"/></div>
                        <div class="fcenter"><input type="text" name="c" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>

                    <div class="subfield repeatable">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_110_d"/></div>
                        <div class="fcenter"><input type="text" name="d" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_110_l"/></div>
                        <div class="fcenter"><input type="text" name="l" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>

                    <div class="subfield repeatable">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_110_n"/></div>
                        <div class="fcenter"><input type="text" name="n" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>
                </fieldset>

                <fieldset class="datafield repeatable" data="410">
                    <legend><i18n:getText module="biblivre3" textKey="a_410"/></legend>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_410_a"/></div>
                        <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>
                </fieldset>

                <fieldset class="datafield repeatable" data="670">
                    <legend><i18n:getText module="biblivre3" textKey="a_670"/></legend>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_670_a"/></div>
                        <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput"/></div>
                        <div class="clear"></div>
                    </div>

                    <div class="subfield">
                        <div class="fleft"><i18n:getText module="biblivre3" textKey="a_670_b"/></div>
                        <div class="fcenter"><textarea name="b" class="finput"></textarea></div>
                        <div class="clear"></div>
                    </div>
                </fieldset>
            </div>
        </div>

        <div class="spacer"></div>
        <div class="submit_buttons">
            <button type="button" onclick="Core.changeTab('search', Authorities.tabHandler);"><i18n:getText module="biblivre3" textKey="BUTTON_CANCEL" /></button>
            <button type="button" onclick="Core.tempDisable(this); Authorities.save();"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE" /></button>
            <div class="clear"></div>
        </div>

        <div class="navigation_bottom">
            <button class="navigation_button_left" type="button" onclick="Authorities.searchNavigate('form', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="Authorities.searchNavigate('form', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>
    </div>

    <div class="tab_body hidden" rel="marc">
        <div class="navigation_top">
            <button class="navigation_button_left" type="button" onclick="Authorities.searchNavigate('marc', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="Authorities.searchNavigate('marc', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <h1 class="tab_inner_title"></h1>
            <div class="clear"></div>
        </div>

        <div class="center">
            <textarea style="width:700px; margin-top: 20px; margin-bottom: 10px;" rows="17" name="freemarc" id="freemarc" ></textarea>
        </div>

        <div class="spacer"></div>
        <div class="submit_buttons">
            <button type="button" onclick="Core.changeTab('search', Authorities.tabHandler);"><i18n:getText module="biblivre3" textKey="BUTTON_CANCEL" /></button>
            <button type="button" onclick="Core.tempDisable(this); Authorities.save();"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE" /></button>
            <div class="clear"></div>
        </div>

        <div class="navigation_bottom">
            <button class="navigation_button_left" type="button" onclick="Authorities.searchNavigate('marc', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="Authorities.searchNavigate('marc', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>
    </div>

    <div class="tab_footer" rel="search">
        <div class="paging search_paging"></div>

        <div class="search_print" style="text-align: center; margin-bottom: 10px;"><button type="button" onclick="window.print();"><i18n:getText module="biblivre3" textKey="BUTTON_PRINT" /></button></div>

        <div id="search_results" class="box_holder">
            <div class="template pointer">
                <div class="box" rel="{serial}" onclick="Core.changeTab('form', Authorities.tabHandler, '{serial}');">
                    <div class="box_content_left">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_NAME" />:</b> {name}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_SERIAL" />:</b> {serial}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_CREATION_DATE" />:</b> {created|Datetime}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_LAST_UPDATE" />:</b> {modified|Datetime}<br/>
                    </div>
                    <div class="box_content_right">
                        <div><button type="button" onclick="Core.runStopingEvent(event, Authorities.deleteAuthor, ['{serial}', this]);"><i18n:getText module="biblivre3" textKey="BUTTON_DELETE" /></button></div>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>

        <div class="paging search_paging"></div>
    </div>
</layout:body>