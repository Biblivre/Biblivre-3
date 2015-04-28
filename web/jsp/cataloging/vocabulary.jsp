<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<i18n:setInitialLocale />

<layout:head>
    <link rel="stylesheet" type="text/css" href="css/cataloging.css" />
    <link rel="stylesheet" media="print" type="text/css" href="css/print.css" />

    <script type="text/javascript" src="scripts/json.js"></script>
    <script type="text/javascript" src="scripts/cataloging.js"></script>
    <script type="text/javascript" src="scripts/vocabulary.js"></script>

    <script type="text/javascript">
        $(document).ready(function() {
            Cataloging.initMarcHelp('vocabulary');
            Cataloging.initRepeatableDataFields();
            Cataloging.initRepeatableSubFields();

            Cataloging.toggleMarcNumbering({ checked: true });
        });
    </script>
</layout:head>

<layout:body thisPage="cataloging_vocabulary">
    <div class="spacer"></div>

    <div class="tab tab_selected" rel="search" onclick="Core.changeTab('search', Vocabulary.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_SEARCH"/>
    </div>

    <div class="tab" rel="form" onclick="Core.changeTab('form', Vocabulary.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_FORM"/>
    </div>

    <div class="tab" rel="marc" onclick="Core.changeTab('marc', Vocabulary.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_MARC"/>
    </div>
    <div class="tab_close"></div>

    <div class="tab_body" rel="search">
        <div id="search_box" class="search_box">
            <table onkeypress="Core.submitKeyPress(event, Vocabulary.searchSubmit);">
                <tr class="auth_search_term">
                    <td class="label_1_biblio"><i18n:getText module="biblivre3" textKey="LABEL_SEARCH_CONTAINING_TERM" /></td>
                    <td class="label_2_biblio"><input type="text" class="input_text input_term" name="search_term"/></td>
                    <td class="label_3_biblio">
                        <i18n:getText module="biblivre3" textKey="LABEL_SEARCH_ON_ATTRIBUTE" />
                        <select name="search_type">
                            <option value="150"><i18n:getText module="biblivre3" textKey="LABEL_TERM_TE"/></option>
                            <option value="450"><i18n:getText module="biblivre3" textKey="LABEL_TERM_UP"/></option>
                            <option value="550"><i18n:getText module="biblivre3" textKey="LABEL_TERM_TG"/></option>
                            <option value="360"><i18n:getText module="biblivre3" textKey="LABEL_TERM_VT_TA_TR"/></option>
                        </select>
                    </td>
                </tr>
                <tr class="form_buttons">
                    <td colspan="3">
                        <div class="search_list_all">
                            <button type="button" onclick="Vocabulary.searchSubmit(true);"><i18n:getText module="biblivre3" textKey="BUTTON_LIST_ALL" /></button>
                        </div>
                        <div class="search_submit">
                            <button type="button" onclick="Vocabulary.searchSubmit();"><i18n:getText module="biblivre3" textKey="BUTTON_SEARCH" /></button>
                        </div>
                        <div class="search_add">
                            <button type="button" onclick="Vocabulary.createNew();"><i18n:getText module="biblivre3" textKey="BUTTON_NEW" /></button>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    </div>

    <div class="tab_body hidden" rel="form">
        <div class="navigation_top">
            <button class="navigation_button_left" type="button" onclick="Vocabulary.searchNavigate('form', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="Vocabulary.searchNavigate('form', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>

        <div class="toggles">
            <i18n:getText module="biblivre3" textKey="LABEL_SHOW_MARC_TAG_NUMBERS"/>
            <input type="checkbox" class="show_marc" onclick="Cataloging.toggleMarcNumbering(this);"/>
        </div>

        <div style="font-size: 12px;">
            <div class="clear"></div>

            <fieldset class="datafield" data="040">
                <legend><i18n:getText module="biblivre3" textKey="v_040"/></legend>
                <div class="subfield">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_040_a"/></div>
                    <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput" /></div>
                    <div class="clear"></div>
                </div>
                <div class="subfield">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_040_b"/></div>
                    <div class="fcenter"><input type="text" name="b" maxlength="256" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="subfield">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_040_c"/></div>
                    <div class="fcenter"><input type="text" name="c" maxlength="256" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="subfield repeatable">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_040_d"/></div>
                    <div class="fcenter"><input type="text" name="d" maxlength="256" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="subfield repeatable">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_040_e"/></div>
                    <div class="fcenter"><input type="text" name="e" maxlength="256" class="finput"/></div>
                    <div class="clear"></div>
                </div>
            </fieldset>

            <fieldset class="datafield dont_show_help" data="150">
                <legend><i18n:getText module="biblivre3" textKey="v_150"/></legend>
                <div class="subfield">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_150_a"/></div>
                    <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput" /></div>
                    <div class="clear"></div>
                </div>
                <div class="subfield">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_150_i"/></div>
                    <div class="fcenter"><input type="text" name="i" maxlength="256" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="subfield repeatable">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_150_x"/></div>
                    <div class="fcenter"><input type="text" name="x" maxlength="256" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="subfield repeatable">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_150_y"/></div>
                    <div class="fcenter"><input type="text" name="y" maxlength="256" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="subfield repeatable">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_150_z"/></div>
                    <div class="fcenter"><input type="text" name="z" maxlength="256" class="finput"/></div>
                    <div class="clear"></div>
                </div>
            </fieldset>

            <fieldset class="datafield repeatable dont_show_help" data="450">
                <legend><i18n:getText module="biblivre3" textKey="v_450"/></legend>
                <div class="subfield">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_450_a"/></div>
                    <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput" /></div>
                    <div class="clear"></div>
                </div>
            </fieldset>

            <fieldset class="datafield repeatable dont_show_help" data="550">
                <legend><i18n:getText module="biblivre3" textKey="v_550"/></legend>
                <div class="subfield">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_550_a"/></div>
                    <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput" /></div>
                    <div class="clear"></div>
                </div>
                <div class="subfield repeatable">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_550_x"/></div>
                    <div class="fcenter"><input type="text" name="x" maxlength="256" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="subfield repeatable">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_550_y"/></div>
                    <div class="fcenter"><input type="text" name="y" maxlength="256" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="subfield repeatable">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_550_z"/></div>
                    <div class="fcenter"><input type="text" name="z" maxlength="256" class="finput"/></div>
                    <div class="clear"></div>
                </div>
            </fieldset>

            <fieldset class="datafield repeatable dont_show_help" data="360">
                <legend><i18n:getText module="biblivre3" textKey="v_360"/></legend>
                <div class="subfield">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_360_a"/></div>
                    <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput" /></div>
                    <div class="clear"></div>
                </div>
                <div class="subfield repeatable">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_360_x"/></div>
                    <div class="fcenter"><input type="text" name="x" maxlength="256" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="subfield repeatable">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_360_y"/></div>
                    <div class="fcenter"><input type="text" name="y" maxlength="256" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="subfield repeatable">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_360_z"/></div>
                    <div class="fcenter"><input type="text" name="z" maxlength="256" class="finput"/></div>
                    <div class="clear"></div>
                </div>
            </fieldset>

            <fieldset class="datafield dont_show_help" data="670">
                <legend><i18n:getText module="biblivre3" textKey="v_670"/></legend>
                <div class="subfield">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_670_a"/></div>
                    <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput" /></div>
                    <div class="clear"></div>
                </div>
            </fieldset>

            <fieldset class="datafield repeatable" data="680">
                <legend><i18n:getText module="biblivre3" textKey="v_680"/></legend>
                <div class="subfield">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_680_a"/></div>
                    <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput" /></div>
                    <div class="clear"></div>
                </div>
            </fieldset>

            <fieldset class="datafield repeatable" data="685">
                <legend><i18n:getText module="biblivre3" textKey="v_685"/></legend>
                <div class="subfield">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_685_i"/></div>
                    <div class="fcenter"><input type="text" name="i" maxlength="256" class="finput" /></div>
                    <div class="clear"></div>
                </div>
            </fieldset>

            <fieldset class="datafield" data="750">
                <legend><i18n:getText module="biblivre3" textKey="v_750"/></legend>
                <div class="indicator">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_750_i1"/></div>
                    <div class="fcenter">
                        <select name="ind1">
                            <option value="0"><i18n:getText module="biblivre3" textKey="v_750_i1_0"/></option>
                            <option value="1"><i18n:getText module="biblivre3" textKey="v_750_i1_1"/></option>
                            <option value="2"><i18n:getText module="biblivre3" textKey="v_750_i1_2"/></option>
                        </select>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="indicator">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_750_i2"/></div>
                    <div class="fcenter">
                        <select name="ind2">
                            <option value="0"><i18n:getText module="biblivre3" textKey="v_750_i2_0"/></option>
                            <option value="4"><i18n:getText module="biblivre3" textKey="v_750_i2_4"/></option>
                        </select>
                    </div>
                    <div class="clear"></div>
                </div>
                <div class="subfield">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_750_a"/></div>
                    <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput" /></div>
                    <div class="clear"></div>
                </div>
                <div class="subfield repeatable">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_750_x"/></div>
                    <div class="fcenter"><input type="text" name="x" maxlength="256" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="subfield repeatable">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_750_y"/></div>
                    <div class="fcenter"><input type="text" name="y" maxlength="256" class="finput"/></div>
                    <div class="clear"></div>
                </div>
                <div class="subfield repeatable">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_750_z"/></div>
                    <div class="fcenter"><input type="text" name="z" maxlength="256" class="finput"/></div>
                    <div class="clear"></div>
                </div>
            </fieldset>

            <fieldset class="datafield dont_show_help" data="913">
                <legend><i18n:getText module="biblivre3" textKey="v_913"/></legend>
                <div class="subfield">
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="v_913_a"/></div>
                    <div class="fcenter"><input type="text" name="a" maxlength="256" class="finput" /></div>
                    <div class="clear"></div>
                </div>
            </fieldset>

        </div>

        <div class="spacer"></div>
        <div class="submit_buttons">
            <button type="button" onclick="Core.changeTab('search', Vocabulary.tabHandler);"><i18n:getText module="biblivre3" textKey="BUTTON_CANCEL" /></button>
            <button type="button" onclick="Core.tempDisable(this); Vocabulary.save();"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE" /></button>
            <div class="clear"></div>
        </div>


        <div class="navigation_bottom">
            <button class="navigation_button_left" type="button" onclick="Vocabulary.searchNavigate('form', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="Vocabulary.searchNavigate('form', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>
    </div>


    <div class="tab_body hidden" rel="marc">
        <div class="navigation_top">
            <button class="navigation_button_left" type="button" onclick="Vocabulary.searchNavigate('marc', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="Vocabulary.searchNavigate('marc', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>

        <div style="text-align: center; margin-bottom: 10px;">
            <textarea style="width:700px; margin-top: 20px;" rows="17" name="freemarc" id="freemarc" ></textarea>
        </div>

        <div class="spacer"></div>
        <div class="submit_buttons">
            <button type="button" onclick="Core.changeTab('search', Vocabulary.tabHandler);"><i18n:getText module="biblivre3" textKey="BUTTON_CANCEL" /></button>
            <button type="button" onclick="Core.tempDisable(this); Vocabulary.save();"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE" /></button>
            <div class="clear"></div>
        </div>

        <div class="navigation_bottom">
            <button class="navigation_button_left" type="button" onclick="Vocabulary.searchNavigate('marc', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="Vocabulary.searchNavigate('marc', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>
    </div>


    <div class="tab_footer" rel="search">
        <div class="paging search_paging"></div>

        <div class="search_print" style="text-align: center; margin-bottom: 10px;"><button type="button" onclick="window.print();"><i18n:getText module="biblivre3" textKey="BUTTON_PRINT" /></button></div>

        <div id="search_results" class="box_holder">
            <div class="template pointer">
                <div class="box" rel="{serial}" onclick="Core.changeTab('form', Vocabulary.tabHandler, '{serial}');">
                    <div class="box_content_left">
                        <b><i18n:getText module="biblivre3" textKey="v_150" />:</b> {term}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_SERIAL" />:</b> {serial}<br/>
                        {{created|Date}<b><i18n:getText module="biblivre3" textKey="LABEL_DATE" />:</b> %%<br/>}
                    </div>
                    <div class="box_content_right">
                        <div><button type="button" onclick="Core.runStopingEvent(event, Vocabulary.deleteRecord, ['{serial}', this, event]);"><i18n:getText module="biblivre3" textKey="BUTTON_DELETE" /></button></div>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>

        <div class="paging search_paging"></div>
    </div>
</layout:body>