<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="layout" uri="/WEB-INF/tlds/layout.tld" %>
<%@ taglib prefix="i18n" uri="/WEB-INF/tlds/i18n.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<i18n:setInitialLocale />

<layout:head>
    <link rel="stylesheet" type="text/css" href="css/cataloging.css" />
    <link rel="stylesheet" media="print" type="text/css" href="css/print.css" />

    <script type="text/javascript" src="scripts/json.js"></script>
    <script type="text/javascript" src="scripts/cataloging.js"></script>
    <script type="text/javascript" src="scripts/bibliographic.js"></script>
    <script type="text/javascript" src="scripts/ajaxfileupload.js"></script>

    <script type="text/javascript">
        $(document).ready(function() {
            Bibliographic.updateDatabaseCount();
            Cataloging.initMarcHelp('biblio');
            Cataloging.initRepeatableDataFields();
            Cataloging.initRepeatableSubFields();

            Bibliographic.initUploadField();

            Bibliographic.initAutoCompleteField();

            Cataloging.toggleMarcNumbering({ checked: true });
        });
    </script>
</layout:head>

<layout:body thisPage="cataloging_biblio">
    <div class="spacer"></div>

    <div class="biblio_database">
        <i18n:getText module="biblivre3" textKey="LABEL_BIBLIO_DATABASE"/>:
        <select id="biblio_database" onchange="Bibliographic.onDatabaseChange();">
            <option value="MAIN"><i18n:getText module="biblivre3" textKey="LABEL_MAIN_BASE"/></option>
            <option value="WORK"><i18n:getText module="biblivre3" textKey="LABEL_WORK_BASE"/></option>
        </select>
        &#160;&#160;&#160;
        <i18n:getText module="biblivre3" textKey="LABEL_TOTAL_RECORDS"/>:
        <span id="biblio_database_count"></span><br/>
        <span class="small"><i18n:getText module="biblivre3" textKey="LABEL_DATABASE_SWITCH_WARNING"/></span>
    </div>

    <div class="spacer"></div>
    <div class="spacer"></div>

    <div class="tab tab_selected" rel="search" onclick="Core.changeTab('search', Bibliographic.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_SEARCH"/>
    </div>

    <div class="tab" rel="record" onclick="Core.changeTab('record', Bibliographic.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_RECORD"/>
    </div>

    <div class="tab" rel="form" onclick="Core.changeTab('form', Bibliographic.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_FORM"/>
    </div>

    <div class="tab" rel="marc" onclick="Core.changeTab('marc', Bibliographic.tabHandler);">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_MARC"/>
    </div>

    <div class="tab" rel="holding_form" onclick="Core.changeTab('holding_form', Bibliographic.tabHandler);" style="display: none;">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_FORM"/>
    </div>

    <div class="tab" rel="holding_marc" onclick="Core.changeTab('holding_marc', Bibliographic.tabHandler);" style="display: none;">
        <i18n:getText module="biblivre3" textKey="TAB_BIBLIO_MARC"/>
    </div>

    <div class="tab_close"></div>




    <div class="tab_body" rel="search">
        <div id="biblio_search_box" class="search_box">
            <table onkeypress="Core.submitKeyPress(event, Bibliographic.searchSubmit);">

                <tr class="biblio_search_term">
                    <td class="label_1_biblio"><i18n:getText module="biblivre3" textKey="LABEL_MATERIAL" /></td>
                    <td class="label_2_biblio">
                        <select class="input_select input_material" name="ITEM_TYPE">
                            <%@ include file="/includes/material_types.jsp" %>
                        </select>
                    </td>
                    <td class="label_3_biblio">&#160;</td>
                </tr>

                <tr class="biblio_search_term dont_clear">
                    <td class="label_1_biblio"><i18n:getText module="biblivre3" textKey="LABEL_SEARCH_CONTAINING_TERM" /></td>
                    <td class="label_2_biblio"><input type="text" class="input_text biblio_input_term" name="SEARCH_TERM"/></td>
                    <td class="label_3_biblio">
                        <i18n:getText module="biblivre3" textKey="LABEL_SEARCH_ON_ATTRIBUTE" />
                        <select class="input_select input_atribute" name="SEARCH_ATTR">
                            <%@ include file="/includes/search_terms.jsp" %>
                        </select>
                        &#160;&#160;
                        <button type="button" style="width: 80px;" onclick="Bibliographic.searchAddTerm();"><i18n:getText module="biblivre3" textKey="BUTTON_REPEAT" /></button>
                    </td>
                </tr>

                <tr id="biblio_search_term_template" class="biblio_search_term template dont_clear">
                    <td class="label_1_biblio">
                        <select disabled="true" name="BOOL_OP" onchange="$(this).parents('tr').find('input.biblio_input_term').focus();">
                            <option value="AND"><i18n:getText module="biblivre3" textKey="VALUE_AND" /></option>
                            <option value="OR"><i18n:getText module="biblivre3" textKey="VALUE_OR" /></option>
                            <option value="AND_NOT"><i18n:getText module="biblivre3" textKey="VALUE_AND_NOT" /></option>
                        </select> <i18n:getText module="biblivre3" textKey="LABEL_SEARCH_CONTAINING_TERM" />
                    </td>
                    <td class="label_2_biblio"><input disabled="true" type="text" class="input_text biblio_input_term" name="SEARCH_TERM"/></td>
                    <td class="label_3_biblio">
                        <i18n:getText module="biblivre3" textKey="LABEL_SEARCH_ON_ATTRIBUTE" />
                        <select disabled="true" class="input_select input_atribute" name="SEARCH_ATTR">
                            <%@ include file="/includes/search_terms.jsp" %>
                        </select>
                        &#160;&#160;
                        <button type="button" style="width: 80px;" onclick="$(this).parents('tr.biblio_search_term').remove();"><i18n:getText module="biblivre3" textKey="BUTTON_REMOVE" /></button>
                    </td>
                </tr>

                <tr class="form_buttons">
                    <td colspan="3">
                        <div class="search_list_all">
                            <button type="button" onclick="Bibliographic.searchSubmit(true);"><i18n:getText module="biblivre3" textKey="BUTTON_LIST_ALL" /></button>
                        </div>
                        <div class="search_submit">
                            <button type="button" onclick="Bibliographic.searchSubmit();"><i18n:getText module="biblivre3" textKey="BUTTON_SEARCH" /></button>
                        </div>
                        <div class="search_add">
                            <button type="button" onclick="Bibliographic.createNew();"><i18n:getText module="biblivre3" textKey="BUTTON_NEW" /></button>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    </div>






    <div class="tab_body hidden" rel="record">
        <div class="navigation_top">
            <button class="navigation_button_left" type="button" onclick="Bibliographic.searchNavigate('record', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="Bibliographic.searchNavigate('record', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>

        <h1 class="tab_inner_title"></h1>

        <table class="biblio_record_table">
            <tbody class="tab_inner_body" id="record_results">
                <tr class="template">
                    <td class="biblio_record_table_col1">{label}:</td>
                    <td class="biblio_record_table_col2">{value}</td>
                </tr>
            </tbody>
        </table>

        <div class="spacer2"></div>

        <h1><i18n:getText module="biblivre3" textKey="TITLE_FILES" /></h1>

        <div class="center">
            <span class="small"><i18n:getText module="biblivre3" textKey="LABEL_HOW_TO_REMOVE_FILES"/></span>
        </div>

        <div class="spacer2"></div>

        <ul id="links_results" class="tab_inner_links">
            <li class="template">
                <a {uri|href} target="_blank">{name}</a>
            </li>
        </ul>

        <div class="spacer2"></div>

        <div class="center" style="font-size: 12px;">
            <i18n:getText module="biblivre3" textKey="LABEL_NEW_FILE_DESCRIPTION" /> <input type="text" name="file_description" id="file_description">
            <button type="button" class="file_upload_button"><i18n:getText module="biblivre3" textKey="BUTTON_SEND_FILE" /></button>
        </div>

        <div class="spacer"></div>
        <div class="spacer"></div>

        <h1><i18n:getText module="biblivre3" textKey="TITLE_HOLDINGS" /></h1>

        <div class="spacer"></div>

        <div class="center">
            <button type="button" onclick="Core.tempDisable(this); BibliographicHolding.createNew();"><i18n:getText module="biblivre3" textKey="BUTTON_NEW_HOLDING" /></button>
        </div>

        <div class="spacer"></div>

        <div id="holding_results" class="box_holder">
            <div class="holding_result template pointer">
                <div class="box" rel="{serial}" onclick="BibliographicHolding.edit('{serial}');">
                    <div class="box_content_left">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_ASSET_HOLDING" />:</b> {assetHolding}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_LENT_STATE" />:</b> {lent}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_AVAILABILITY" />:</b> {available_text}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_090" />:</b> {location}<br/>
                    </div>

                    <div class="box_content_right">
                        <div><button type="button" style="width: 230px;" onclick="Core.runStopingEvent(event, BibliographicHolding.deleteHolding, ['{serial}', this]);"><i18n:getText module="biblivre3" textKey="BUTTON_DELETE" /></button></div>
                        <div><button type="button" style="width: 230px;" onclick="Core.runStopingEvent(event, BibliographicHolding.generateLabel, ['{serial}', this]);"><i18n:getText module="biblivre3" textKey="BUTTON_GENERATE_LABEL" /></button></div>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>

        <div class="navigation_bottom">
            <button class="navigation_button_left" type="button" onclick="Bibliographic.searchNavigate('record', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="Bibliographic.searchNavigate('record', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>
    </div>








    <div class="tab_body hidden" rel="form">
        <div class="navigation_top">
            <button class="navigation_button_left" type="button" onclick="Bibliographic.searchNavigate('form', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="Bibliographic.searchNavigate('form', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <h1 class="tab_inner_title"></h1>
            <div class="clear"></div>
        </div>

        <div class="toggles">
            <i18n:getText module="biblivre3" textKey="LABEL_SHOW_MARC_TAG_NUMBERS"/>
            <input type="checkbox" class="show_marc" onclick="Cataloging.toggleMarcNumbering(this);"/>
        </div>

        <div style="font-size: 12px;">
            <div class="clear"></div>

            <fieldset class="noborder">
                <div>
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_MATERIAL"/></div>

                    <div class="fcenter">
                        <select name="material_type" onchange="Cataloging.toggleAreas(this.name, this.value);">
                            <option value=""><i18n:getText module="biblivre3" textKey="VALUE_SELECT_MATERIAL_TYPE" /></option>
                            <option value="book"><i18n:getText module="biblivre3" textKey="VALUE_BOOK" /></option>
                            <option value="bookp"><i18n:getText module="biblivre3" textKey="VALUE_BOOKP" /></option>
                            <option value="bookt"><i18n:getText module="biblivre3" textKey="VALUE_BOOKT" /></option>
                            <option value="serial"><i18n:getText module="biblivre3" textKey="VALUE_SERIAL"/></option>
                            <option value="seriar"><i18n:getText module="biblivre3" textKey="VALUE_SERIAR"/></option>
                            <option value="bookm"><i18n:getText module="biblivre3" textKey="VALUE_BOOKM" /></option>
                            <option value="photo"><i18n:getText module="biblivre3" textKey="VALUE_PHOTO" /></option>
                            <option value="maps"><i18n:getText module="biblivre3" textKey="VALUE_MAPS" /></option>
                            <option value="movie"><i18n:getText module="biblivre3" textKey="VALUE_MOVIE" /></option>
                            <option value="sound"><i18n:getText module="biblivre3" textKey="VALUE_SOUND" /></option>
                            <option value="music"><i18n:getText module="biblivre3" textKey="VALUE_MUSIC" /></option>
                            <option value="cfiles"><i18n:getText module="biblivre3" textKey="VALUE_CFILES"/></option>
                            <option value="obj3d"><i18n:getText module="biblivre3" textKey="VALUE_OBJ3D" /></option>
                        </select>
                    </div>

                    <div class="clear"></div>
                </div>
            </fieldset>

            <div class="material_type" data="book,bookp,photo,movie,sound,music,maps,obj3d,cfiles,bookt,bookm,seriar" style="display:none;">
                <%@ include file="/includes/inc_1xx_author.jsp" %>
            </div>

            <div class="material_type" data="book,bookp,bookt,photo,movie,sound,music,maps,bookm,serial,obj3d,cfiles,seriar" style="display:none;">
                <%@ include file="/includes/inc_2xx_title.jsp" %>
            </div>

            <div class="material_type" data="book" style="display:none;">
                <%@ include file="/includes/inc_020_isbn.jsp" %>
            </div>

            <div class="material_type" data="book,serial,seriar" style="display:none;">
                <%@ include file="/includes/inc_022_issn.jsp" %>
            </div>

            <div class="material_type" data="sound,music" style="display:none;">
                <%@ include file="/includes/inc_029_ismn.jsp" %>
            </div>

            <div class="material_type" data="maps" style="display:none;">
                <%@ include file="/includes/inc_04x_codes.jsp" %>
            </div>

            <div class="material_type" data="book,bookp,bookt,photo,movie,sound,music,maps,bookm,serial,cfiles,seriar" style="display:none;">
                <%@ include file="/includes/inc_040_language.jsp" %>
            </div>

            <div class="material_type" data="book,bookp,bookt,photo,movie,sound,music,maps,bookm,serial,obj3d,cfiles,seriar" style="display:none;">
                <%@ include file="/includes/inc_082_dewey.jsp" %>
            </div>

            <div class="material_type" data="book,bookp,bookt,photo,movie,sound,music,maps,bookm,serial,obj3d,cfiles,seriar" style="display:none;">
                <%@ include file="/includes/inc_090_local.jsp" %>
            </div>

            <div class="material_type" data="book,bookt" style="display:none;">
                <%@ include file="/includes/inc_250_edition.jsp" %>
            </div>

            <div class="material_type" data="maps" style="display:none;">
                <%@ include file="/includes/inc_255_maps.jsp" %>
            </div>

            <div class="material_type" data="cfiles" style="display:none;">
                <%@ include file="/includes/inc_256_cfiles.jsp" %>
            </div>

            <div class="material_type" data="movie" style="display:none;">
                <%@ include file="/includes/inc_257_audiovisual.jsp" %>
            </div>

            <div class="material_type" data="photo" style="display:none;">
                <%@ include file="/includes/inc_258_icon.jsp" %>
            </div>

            <div class="material_type" data="book,bookt,photo,movie,sound,music,bookm,serial,cfiles,seriar" style="display:none;">
                <%@ include file="/includes/inc_260_imprenta.jsp" %>
            </div>

            <div class="material_type" data="maps" style="display:none;">
                <%@ include file="/includes/inc_260_simple_imprenta.jsp" %>
            </div>

            <div class="material_type" data="book,bookt,photo,movie,sound,music,maps,bookm,serial,obj3d,cfiles,seriar" style="display:none;">
                <%@ include file="/includes/inc_300_description.jsp" %>
            </div>

            <div class="material_type" data="movie,sound" style="display:none;">
                <%@ include file="/includes/inc_306_playtime.jsp" %>
            </div>

            <div class="material_type" data="book,photo,movie,sound,music,serial,obj3d,seriar" style="display:none;">
                <%@ include file="/includes/inc_490_serie.jsp" %>
            </div>

            <div class="material_type" data="maps" style="display:none;">
                <%@ include file="/includes/inc_34x_maps.jsp" %>
            </div>

            <div class="material_type" data="book,bookp,bookt,photo,movie,sound,music,maps,bookm,serial,obj3d,cfiles,seriar" style="display:none;">
                <%@ include file="/includes/inc_5xx_notes.jsp" %>
            </div>

            <div class="material_type" data="bookt" style="display:none;">
                <%@ include file="/includes/inc_502_notes.jsp" %>
            </div>

            <div class="material_type" data="book,bookp,bookt,photo,movie,sound,music,maps,bookm,serial,obj3d,cfiles,seriar" style="display:none;">
                <%@ include file="/includes/inc_6xx_subject.jsp" %>
            </div>

            <div class="material_type" data="bookt" style="display:none;">
                <%@ include file="/includes/inc_700_sec.jsp" %>
            </div>

            <div class="material_type" data="book,bookp,bookt,photo,movie,sound,music,maps,bookm,serial,obj3d,cfiles,seriar" style="display:none;">
                <%@ include file="/includes/inc_7xx_sec.jsp" %>
            </div>
        </div>

        <div class="spacer"></div>

        <div class="submit_buttons">
            <button type="button" class="biblio_save_as" onclick="Core.tempDisable(this); Bibliographic.saveAsNew();"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE_AS" /></button>
            <button type="button" onclick="Core.changeTab('search', Bibliographic.tabHandler);"><i18n:getText module="biblivre3" textKey="BUTTON_CANCEL" /></button>
            <button type="button" onclick="Core.tempDisable(this); Bibliographic.save();"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE" /></button>
            <div class="clear"></div>
        </div>

        <div class="navigation_bottom">
            <button class="navigation_button_left" type="button" onclick="Bibliographic.searchNavigate('form', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="Bibliographic.searchNavigate('form', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>
    </div>







    <div class="tab_body hidden" rel="marc">
        <div class="navigation_top">
            <button class="navigation_button_left" type="button" onclick="Bibliographic.searchNavigate('marc', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="Bibliographic.searchNavigate('marc', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <h1 class="tab_inner_title"></h1>
            <div class="clear"></div>
        </div>

        <fieldset class="noborder" style="font-size: 12px;">
            <div>
                <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_MATERIAL"/></div>

                <div class="fcenter">
                    <select name="material_type">
                        <option value=""><i18n:getText module="biblivre3" textKey="VALUE_SELECT_MATERIAL_TYPE" /></option>
                        <option value="book"><i18n:getText module="biblivre3" textKey="VALUE_BOOK" /></option>
                        <option value="bookp"><i18n:getText module="biblivre3" textKey="VALUE_BOOKP" /></option>
                        <option value="bookt"><i18n:getText module="biblivre3" textKey="VALUE_BOOKT" /></option>
                        <option value="serial"><i18n:getText module="biblivre3" textKey="VALUE_SERIAL"/></option>
                        <option value="seriar"><i18n:getText module="biblivre3" textKey="VALUE_SERIAR"/></option>
                        <option value="bookm"><i18n:getText module="biblivre3" textKey="VALUE_BOOKM" /></option>
                        <option value="photo"><i18n:getText module="biblivre3" textKey="VALUE_PHOTO" /></option>
                        <option value="maps"><i18n:getText module="biblivre3" textKey="VALUE_MAPS" /></option>
                        <option value="movie"><i18n:getText module="biblivre3" textKey="VALUE_MOVIE" /></option>
                        <option value="sound"><i18n:getText module="biblivre3" textKey="VALUE_SOUND" /></option>
                        <option value="music"><i18n:getText module="biblivre3" textKey="VALUE_MUSIC" /></option>
                        <option value="cfiles"><i18n:getText module="biblivre3" textKey="VALUE_CFILES"/></option>
                        <option value="obj3d"><i18n:getText module="biblivre3" textKey="VALUE_OBJ3D" /></option>
                    </select>
                </div>

                <div class="clear"></div>
            </div>
        </fieldset>


        <div style="text-align: center; margin-bottom: 10px;">
            <textarea style="width:700px; margin-top: 20px;" rows="17" name="freemarc" id="freemarc" ></textarea>
        </div>

        <div class="spacer"></div>

        <div class="submit_buttons">
            <button type="button" class="biblio_save_as" onclick="Core.tempDisable(this); Bibliographic.saveAsNew();"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE_AS" /></button>
            <button type="button" onclick="Core.changeTab('search', Bibliographic.tabHandler);"><i18n:getText module="biblivre3" textKey="BUTTON_CANCEL" /></button>
            <button type="button" onclick="Core.tempDisable(this); Bibliographic.save();"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE" /></button>
            <div class="clear"></div>
        </div>

        <div class="navigation_bottom">
            <button class="navigation_button_left" type="button" onclick="Bibliographic.searchNavigate('marc', 'prev');"><i18n:getText module="biblivre3" textKey="BUTTON_PREVIOUS" /></button>
            <button class="navigation_button_right" type="button" onclick="Bibliographic.searchNavigate('marc', 'next');"><i18n:getText module="biblivre3" textKey="BUTTON_NEXT" /></button>
            <div class="clear"></div>
        </div>
    </div>







    <div class="tab_body hidden" rel="holding_form">
        <div class="toggles">
            <i18n:getText module="biblivre3" textKey="LABEL_SHOW_MARC_TAG_NUMBERS"/>
            <input type="checkbox" class="show_marc" onclick="Cataloging.toggleMarcNumbering(this);"/>
        </div>

        <div style="font-size: 12px;">
            <div class="clear"></div>

            <fieldset class="noborder">
                <div>
                    <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_AVAILABILITY"/></div>

                    <div class="fcenter">
                        <select name="available">
                            <c:forEach var="availability" items="<%=biblivre3.enums.Availability.values()%>" >
                                <option value="${availability.ordinal}">
                                    <i18n:getText module="biblivre3" textKey="${availability.label}" />
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="clear"></div>
                </div>
            </fieldset>

            <%@ include file="/includes/inc_holding.jsp" %>
        </div>

        <div class="spacer"></div>
        <div class="submit_buttons">
            <button type="button" class="holding_save_as" onclick="Core.tempDisable(this); BibliographicHolding.saveAsNew();"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE_AS" /></button>
            <button type="button" onclick="Core.changeTab('record', Bibliographic.tabHandler);"><i18n:getText module="biblivre3" textKey="BUTTON_CANCEL" /></button>
            <button type="button" onclick="Core.tempDisable(this); BibliographicHolding.save();"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE" /></button>
            <div class="clear"></div>
        </div>
    </div>







    <div class="tab_body hidden" rel="holding_marc">
        <fieldset class="noborder">
            <div style="font-size: 12px;">
                <div class="fleft"><i18n:getText module="biblivre3" textKey="LABEL_AVAILABILITY"/></div>

                <div class="fcenter">
                    <select name="available">
                        <c:forEach var="availability" items="<%=biblivre3.enums.Availability.values()%>" >
                            <option value="${availability.ordinal}">
                                <i18n:getText module="biblivre3" textKey="${availability.label}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="clear"></div>
            </div>
        </fieldset>

        <div style="text-align: center; margin-bottom: 10px;">
            <textarea style="width:700px; margin-top: 20px;" rows="17" name="holding_freemarc" id="holding_freemarc" ></textarea>
        </div>

        <div class="spacer"></div>
        <div class="submit_buttons">
            <button type="button" class="holding_save_as" onclick="Core.tempDisable(this); BibliographicHolding.saveAsNew();"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE_AS" /></button>
            <button type="button" onclick="Core.changeTab('record', Bibliographic.tabHandler);"><i18n:getText module="biblivre3" textKey="BUTTON_CANCEL" /></button>
            <button type="button" onclick="Core.tempDisable(this); BibliographicHolding.save();"><i18n:getText module="biblivre3" textKey="BUTTON_SAVE" /></button>
            <div class="clear"></div>
        </div>
    </div>








    <div class="tab_footer" rel="search">
        <div id="export_box" class="border lightbackground">
            <div id="export_list">
                <div class="export_item template">&bull; {title} <a href="javascript:void(0);" onclick="Bibliographic.removeFromExportList('{serial}');">[Remover]</a></div>
            </div>
            <div style="text-align: right; margin-top: 10px;">
                <button type="button" onclick="Bibliographic.clearExportList();"><i18n:getText module="biblivre3" textKey="BUTTON_CLEAR_EXPORT_LIST" /></button>
                <button type="button" onclick="Core.tempDisable(this); Bibliographic.exportRecords();" style="margin-left: 10px"><i18n:getText module="biblivre3" textKey="BUTTON_EXPORT" /></button>
            </div>
            <input type="hidden" id="export_serial_list" name="serial_list" />
            <input type="hidden" id="export_base" name="base" />
        </div>

        <div class="paging search_paging"></div>

        <div class="search_print" style="text-align: center; margin-bottom: 10px;">
            <button type="button" onclick="window.print();"><i18n:getText module="biblivre3" textKey="BUTTON_PRINT" /></button>
            <button type="button" onclick="Bibliographic.addAllToExportList();" style="margin-left: 10px"><i18n:getText module="biblivre3" textKey="BUTTON_EXPORT_ALL" /></button>
        </div>

        <div id="search_results" class="box_holder">
            <div class="template pointer">
                <div class="box" rel="{serial}" onclick="Core.changeTab('record', Bibliographic.tabHandler, '{serial}');">
                    <div class="box_content_left">
                        <b><i18n:getText module="biblivre3" textKey="LABEL_TITLE" />:</b> {title}<br/>
                        <b><i18n:getText module="biblivre3" textKey="LABEL_AUTHOR" />:</b> {author}<br/>
                        {{date}<b><i18n:getText module="biblivre3" textKey="LABEL_DATE" />:</b> %%<br/>}
                        {{location}<b><i18n:getText module="biblivre3" textKey="LABEL_090" />:</b> %%<br/>}
                        {{ISBN}<b><i18n:getText module="biblivre3" textKey="LABEL_ISBN" />:</b> %%<br/>}
                        <b><i18n:getText module="biblivre3" textKey="LABEL_SERIAL" />:</b> {serial}<br/>
                        {{holdings_count}<b><i18n:getText module="biblivre3" textKey="LABEL_HOLDING_COUNT" />:</b> %% &#160;&#160;&#160;}
                        {{holdings_available}<b><i18n:getText module="biblivre3" textKey="LABEL_AVAILABLE_COUNT" />:</b> %% &#160;&#160;&#160;}
                        {{holdings_unavailable}<b><i18n:getText module="biblivre3" textKey="LABEL_UNAVAILABLE_COUNT" />:</b> %% &#160;&#160;&#160;}
                        {{holdings_lent}<b><i18n:getText module="biblivre3" textKey="LABEL_LENT_COUNT" />:</b> %% &#160;&#160;&#160;}
                        {{holdings_reserved}<b><i18n:getText module="biblivre3" textKey="LABEL_RESERVED_COUNT" />:</b> %%}
                    </div>
                    <div class="box_content_right">
                        <div><button type="button" onclick="Core.runStopingEvent(event, Bibliographic.deleteRecord, ['{serial}', this, event]);"><i18n:getText module="biblivre3" textKey="BUTTON_DELETE" /></button></div>
                        <div><button type="button" class="export_button" onclick="Core.runStopingEvent(event, Bibliographic.addToExportList, [$(this).parents('.box').parent()]);"><i18n:getText module="biblivre3" textKey="BUTTON_SELECT_TO_EXPORT" /></button></div>
                    </div>
                    <div class="clear"></div>
                </div>
            </div>
        </div>

        <div class="paging search_paging"></div>
    </div>


    <fieldset class="automatic_holdings" id="automatic_holdings" style="display: none;">
        <legend><i18n:getText module="biblivre3" textKey="LABEL_AUTOMATIC_HOLDING" /></legend>
        <div class="spacer2"></div>
        <table border="0">
            <tr>
                <td><i18n:getText module="biblivre3" textKey="LABEL_NUMBER_HOLDINGS"/>:</td>
                <td><input type="text" value="1" name="quant" /></td>
            </tr>
            <tr>
                <td><i18n:getText module="biblivre3" textKey="LABEL_NUMBER_VOLUME"/>:</td>
                <td>
                    <input type="radio" name="volumes" value="nvol" onclick="Cataloging.automaticHoldingsCheckVolume('nvol', true);" />
                    <input type="text" name="nvol" onfocus="Cataloging.automaticHoldingsCheckVolume('nvol', false);"/>
                </td>
            </tr>
            <tr>
                <td><i18n:getText module="biblivre3" textKey="LABEL_NUMBER_VOLUME_WORK"/>:</td>
                <td>
                    <input type="radio" name="volumes" value="nvol_obra" onclick="Cataloging.automaticHoldingsCheckVolume('nvol_obra', true);" />
                    <input type="text" name="nvol_obra" onfocus="Cataloging.automaticHoldingsCheckVolume('nvol_obra', false);"/>
                </td>
            </tr>
            <tr>
                <td><i18n:getText module="biblivre3" textKey="LABEL_DATE_TUMBLING"/>:</td>
                <td><input type="text" name="dt_tomb" /></td>
            </tr>
            <tr>
                <td><i18n:getText module="biblivre3" textKey="LABEL_DEPOSITORY_LIBRARY"/>:</td>
                <td><input type="text" name="biblio_dep" /></td>
            </tr>
            <tr>
                <td><i18n:getText module="biblivre3" textKey="LABEL_TYPE_ACQUISITION"/>:</td>
                <td><input type="text" name="aquis" value="<i18n:getText module="biblivre3" textKey="LABEL_PURCHASE"/>" /></td>
            </tr>
        </table>
            
        <div class="spacer2"></div>

        <div class="dialog_buttons">
            <button type="button" style="width:170px;" onclick="BibliographicHolding.closeAutomaticHoldingDialog(this);"><i18n:getText module="biblivre3" textKey="BUTTON_DONT_CREATE_HOLDING" /></button>
            <button type="button" style="width:170px;" onclick="Core.tempDisable(this); BibliographicHolding.createAutomaticHolding(this);"><i18n:getText module="biblivre3" textKey="BUTTON_CREATE_HOLDING" /></button>
        </div>
    </fieldset>
</layout:body>