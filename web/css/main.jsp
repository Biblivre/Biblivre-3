<%@page contentType="text/css" pageEncoding="UTF-8"%>
<%@page import="biblivre3.config.Config"%>
<%@page import="biblivre3.config.ConfigurationEnum"%><%
    String borderColor = request.getParameter("bordercolor");
    String headerColor = request.getParameter("headercolor");
    String backgroundColor = request.getParameter("bgcolor");
    String backgroundLightColor = request.getParameter("bglightcolor");
    String menuBackgroundColor = "";

    if (borderColor == null || borderColor.isEmpty()) {
        borderColor = Config.getConfigProperty(ConfigurationEnum.BORDER_COLOR, "08325E");
    }

    if (headerColor == null || headerColor.isEmpty()) {
        headerColor = Config.getConfigProperty(ConfigurationEnum.HEADER_COLOR, "C3DBF5");
    }

    if (backgroundColor == null || backgroundColor.isEmpty()) {
        backgroundColor = Config.getConfigProperty(ConfigurationEnum.BACKGROUND_COLOR, "E3EBF3");
    }

    if (backgroundLightColor == null || backgroundLightColor.isEmpty()) {
        backgroundLightColor = Config.getConfigProperty(ConfigurationEnum.BACKGROUND_LIGHT_COLOR, "F5FAFF");
    }

    String redForeground = headerColor.substring(0, 2);
    String greenForeground = headerColor.substring(2, 4);
    String blueForeground = headerColor.substring(4);

    Double newRed = Integer.parseInt("C9", 16) + (Integer.parseInt(redForeground, 16) - Integer.parseInt("C9", 16)) * 0.7;
    Double newGreen = Integer.parseInt("C9", 16) + (Integer.parseInt(greenForeground, 16) - Integer.parseInt("C9", 16)) * 0.7;
    Double newBlue = Integer.parseInt("C9", 16) + (Integer.parseInt(blueForeground, 16) - Integer.parseInt("C9", 16)) * 0.7;

    menuBackgroundColor = Integer.toHexString(newRed.intValue()) + Integer.toHexString(newGreen.intValue()) + Integer.toHexString(newBlue.intValue());
%>


/* CSS RESET */
html, body, div, span, applet, object, iframe, h1, h2, h3, h4, h5, h6, p, blockquote, pre,
a, abbr, acronym, address, big, cite, code, del, dfn, em, font, img, ins, kbd, q, s, samp,
small, strike, strong, sub, sup, tt, var, b, u, i, center, dl, dt, dd, ol, ul, li,
fieldset, form, label, legend, caption,tbody, tfoot, thead, table, tr, th, td {
    margin: 0;
    padding: 0;
    border: 0;
    outline: 0;
    font-size: 100%;
    vertical-align: baseline;
    background: transparent;
}

table {
    border-collapse: collapse;
    border-spacing: 0;
}

body {
    line-height: 1;
}

ol, ul {
    list-style: none;
}

blockquote, q {
    quotes: none;
}

blockquote:before, blockquote:after, q:before, q:after {
    content: '';
    content: none;
}

:focus {
    outline: 0;
}

ins {
    text-decoration: none;
}

del {
    text-decoration: line-through;
}
/* /CSS RESET */


/* COMMONS */

.clear {
    clear: both;
}

.center {
    text-align: center;
}

.left {
    text-align: left;
}

.right {
    text-align: right;
}

.fleft {
    float: left;
    width: 320px;
    text-align: right;
    padding: 2px 5px 2px 2px;
    line-height: 20px;
}

.fcenter {
    float: left;
    width: 260px;
    padding: 2px;
    text-align: left;
}

.fright {
    float: left;
    text-align:left;
    padding: 2px 2px 2px 10px;
    font-weight: bold;
    line-height: 20px;
}

.finput {
    width: 100%;
}

.fright a {
    font-size: 10px;
    line-height: 20px;
    margin-left: 10px;
}

div.spacer {
    height: 5px;
    line-height: 5px;
    font-size: 1px;
    clear: left;
}

div.spacer2 {
    height: 15px;
    line-height: 15px;
    font-size: 1px;
    clear: left;
}

.pointer {
    cursor: pointer;
}

.hidden {
    display: none;
}

.small {
    font-size: 10px;
    font-weight: normal;
}

.border {
    border: 1px solid #<%= borderColor %>;
}

.lightbackground {
    background-color: #<%= backgroundLightColor %>;
}


.loading {
    font-size: 12px;
    font-weight: bold;
    color: #<%= borderColor %>;
}

/* /COMMONS */


/* LAYOUT BIBLIVRE */
html, body {
    overflow: hidden;
    font-family: Verdana, Tahoma, Arial, Helvetica;
}

a {
    text-decoration: underline;
    color: #<%= borderColor %>;
}

a:hover {
    text-decoration: none;
}

h1 {
    text-align: center;
    font-size: 16px;
    padding: 5px;
}

h2 {
    font-size: 20px;
}

h3 {
    font-size: 14px;
    font-weight: bold;
    margin: 0 0 5px 0;
    padding-top: 0;
}

.help_message {
    border: 1px dotted #<%= borderColor %>;
    background-color: #<%= backgroundLightColor %>;

    padding: 10px;
    margin: 15px 10px 15px 10px;

    font-size: 12px;
    line-height: 16px;
    font-weight: normal;
}

/* /LAYOUT BIBLIVRE */

/* HEADER */
#header {
    position: relative;
    background: #C7C7C7 url('../images/headerbg.png') 0px 0px repeat-x;
    height: 95px;
    width: 100%;

    border-bottom: 1px solid #<%= borderColor %>;
    color: #<%= borderColor %>;

    z-index: 1000;
}

.ie #header {
    height: 96px;
}

#header_overlay {
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    height: 95px;

    z-index: -1;

    background-color: #<%= headerColor %>;
    opacity: 0.7;
    -moz-opacity: 0.7;
    filter: alpha(opacity=70);
}

.small_window #header, .small_window #header_overlay {
    height: 120px;
}

#header_title {
    position: absolute;
    top: 20px;
    left: 240px;
    right: 240px;

    text-align: center;

    font-size: 26px;
    line-height: 26px;
    color: #<%= borderColor %>;
}

.double_line_header #header_title {
    top: 12px !important;
}

.triple_line_header #header_title {
    top: 12px !important;
    font-size: 16px;
    line-height: 20px;
}

#header_subtitle {
  font-size: 15px;
}

.triple_line_header #header_subtitle {
    font-size: 12px;
}

/*
.small_window #header_title {
    text-align: left;
    margin-left: 175px;
    padding-top: 58px;
    font-size: 18px;
}
*/
/* /HEADER */

/* HEADER ITEMS */
#biblivre_logo {
    position: absolute;
    top: 17px;
    left: 15px;
    width: 107px;
    height: 60px;
}

#biblivre_logo {
    background-image: url('../images/biblivre3small.png') !important;
}

.ie6 #biblivre_logo {
    background-image: none !important;
    filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src='images/biblivre3small.png', sizingMethod='crop');
}

#sponsor_logo {
    position: absolute;
    top: 0px;
    left: 140px;
    width: 85px;
    height: 42px;
}

#sponsor_logo {
    background-image: url('../images/logo_itau_bg.png') !important;
}

.ie6 #sponsor_logo {
    background-image: none !important;
    filter: progid:DXImageTransform.Microsoft.AlphaImageLoader(src='images/logo_itau_bg.png', sizingMethod='crop');
}

#sponsor_logo div {
    position: absolute;
    bottom: 0;
    left: 11px;
    width: 63px;
    height: 42px;
}

#logo_time {
    background: #fc6a00;
    font-face: Arial;
    font-size: 16px;
    text-align: center;
    color: #FFF;
    line-height: 17px;
}

.medium_window #biblivre_logo {
    top: 8px;
}

.medium_window #sponsor_logo {
    top: 0px;
}

#logos div {
    position: absolute;
    top: 0;
    right: 0;
}

#language_picker {
    position: absolute;
    top: 8px;
    right: 80px;
    width: 120px;

    text-align: center;
}

#language_picker select {
    margin-top: 5px;
    width: 120px;
}

#language_picker img {
    cursor: pointer;
}
/* /HEADER ITEMS */

/* MENU */
#menu {
    position: absolute;
    width: 100%;
    left: 0;
    bottom: 0;
    z-index: 1;
}

#menu_root {
    width: 780px;
    margin: 0px auto;
}

#menu_root li {
    position: relative;
    float: left;
    padding-right: 20px;
    line-height: 25px;
    height: 25px;

    color: #<%= borderColor %>;

    font-size: 14px;
    font-weight: bold;
    cursor: pointer;
}

#menu_root .logout, #menu_root .login {
    float: right;
    padding-right: 0;
}

#menu_root .login input {
    border: 1px solid #<%= borderColor %>;
    width: 100px;
    margin-right: 5px;
}

#menu_root .logout button, #menu_root .login button {
    border: 0px;
    background: transparent;

    color: #<%= borderColor %>;

    line-height: 25px !important;
    height: 25px !important;
    font-size: 14px;
    font-weight: bold;
    cursor: pointer;
}

#menu_root .login .button_hover {
    color: #388643;
}

#menu_root .logout .button_hover {
    color: #E13939;
}

#menu_root .hover {
}

#menu_root .submenu {
    display: none;
}

#menu_root .hover .submenu {
    display: block !important;
}

#menu_root .submenu {
    position: absolute;
    padding-top: 1px;
    left: -12px;

    border-left: 1px solid #<%= borderColor %>;
    border-bottom: 1px solid #<%= borderColor %>;
    border-right: 1px solid #<%= borderColor %>;

    background-color: #<%= menuBackgroundColor %>;
}

#menu_root .submenu li {
    float: none;
    padding: 4px 10px;
    margin: 0;

    line-height: 14px;
    height: 14px;
    text-align: left;

    white-space: nowrap;
    border: 0;
}

#menu_root .submenu .hover {
    background-color: #FFFFFF;
    color: #<%= borderColor %>;
}

#menu_root .submenu .disabled {
    color: #999999;
    background-color: #<%= menuBackgroundColor %>;
    cursor: default;
}
/* /MENU */

/* CONTENT */
#content_outer {
    width: 100%;
    overflow-x: hidden;
    overflow-y: scroll;
    position: relative;
    background: #<%= backgroundColor %>;
}

.small_window #content_outer {
    overflow-x: auto;
}

#content_outer_scrollbar {
    margin-right: 20px;
}

#content {
    margin: 0 auto;
    width: 780px;
    background: #FFFFFF;
    border-left: 1px solid #<%= borderColor %>;
    border-right: 1px solid #<%= borderColor %>;
    border-bottom: 1px solid #<%= borderColor %>;
}

#copyright {
    width: 780px;
    margin: 0 auto;
    text-align: right;
    font-size: 12px;
    line-height: 20px;
    color: #888888;
}

#copyright a {
    text-decoration: none;
}

#content_inner {
    margin: 0px 10px 0px 10px;
}

#content_inner p {
    margin: 25px 0px 25px 0px;
    font-size: 16px;
}

#content_inner .about {
    font-size: 12px;
    font-weight: bold;
}

#system_warnings {
    border-bottom: 1px solid #<%= borderColor %>;
    padding: 10px;
    font-weight: bold;
    font-size: 14px;
    line-height: 20px;
    background: #FF7F7F;
}

#breadcrumb {
    padding: 10px 10px 5px 10px;
    font-size: 13px;
    font-weight: bold;
}

#message {
    border: 1px solid #<%= borderColor %>;
    margin: 5px 10px 5px 10px;
    font-size: 16px;
    font-weight: bold;
    text-align: center;
}

#message p {
    padding: 5px;
}

.normal {
    background-color: #<%= backgroundColor %>;
}

.warning {
    background-color: #FFCD05;
}

.error {
    background-color: #D22E2E;
    color: #FFFFFF;
}

#footer {
    margin-bottom: 10px;
}
/* /CONTENT */

/* BUTTONS */
button {
    background: #<%= backgroundColor %>;
    border: 1px solid #<%= borderColor %>;
    line-height: 28px;
    height: 30px;

    font-weight: bold;
}

.button_hover {
    background: #<%= backgroundLightColor %>;
    cursor: pointer;
}

.button_clicked {
}
/* /BUTTONS */

/* TABS */
.tab {
     text-align: center;
     float: left;
     min-width: 120px;
     padding: 0px 15px 0px 15px;
     border: 1px solid #<%= borderColor %>;

     margin-right: -1px;
     margin-top: 2px;
     line-height: 28px;
     font-weight: bold;
     background: #<%= backgroundColor %>;
}

.tab_hover {
    background: #<%= backgroundLightColor %>;
    cursor: pointer;
}

.tab_selected {
    margin-top: 0;
    line-height: 30px;
    background: #FFFFFF;
    border-bottom: 1px solid #FFFFFF;
}

.tab_close {
    border-top: 1px solid #FFFFFF;
    border-bottom: 1px solid #<%= borderColor %>;
    line-height: 30px;
    height: 30px;
}

.tab_body {
    clear: both;

    border-left: 1px solid #<%= borderColor %>;
    border-bottom: 1px solid #<%= borderColor %>;
    border-right: 1px solid #<%= borderColor %>;
    margin-top: -1px;
    padding: 10px;
    overflow: hidden;
}

/* /TABS */


/* FIELDSETS & TEMPLATES */

fieldset {
    border: 1px dotted #<%= borderColor %>;
    padding: 5px;
    margin-bottom: 10px;
}

fieldset legend {
    margin-left: 10px;
    padding-left: 5px;
    padding-right: 5px;
    font-weight: bold;
    font-size: 12px;
    background: #FFFFFF;
}

.ie fieldset legend {
    height: 16px;
}

fieldset legend a {
    margin-left: 10px;
    font-size: 10px;
}


.box {
    border: 1px solid #<%= borderColor %>;
    padding: 10px;

    font-size: 12px;
    line-height: 16px;

    position: relative;
    margin-top: 10px;

    clear: both;
}

.colored_box {
    background-color: #<%= backgroundLightColor %> !important;
    border: 1px solid #<%= borderColor %>;
    padding: 10px;

    font-size: 12px;
    line-height: 16px;

    margin-top: 10px;
}

.box_holder .odd {
    background-color: #<%= backgroundLightColor %>;
}

.box_holder .even {
    background-color: #FFFFFF;
}

.box_content_left {
    float: left;
    width: 65%;
}

.box_content_right {
    float: right;
}

.box_content_right button {
    width: 200px;
    margin-bottom: 5px;
}

.box_content_bottom {
    position: absolute;
    bottom: 10px;
    left: 10px;
}

.box_content_bottom button {
    width: 200px;
    margin-right: 20px;
}

.template {
    display: none;
}

.template_empty_message {
    display: none;
}

.empty .template_empty_message {
    display: block;
}

.template_empty_message {
    font-size: 12px;
    text-align: center;
}

.show_more {
    font-size: 12px;
    font-weight: bold;
    cursor: pointer;
}



/* /FIELDSETS & TEMPLATES */

/* PAGING */

.navigation_bottom {
    margin-top: 10px;
}

.navigation_button_left {
    width: 110px;
    float: left;
}

.navigation_button_right {
    width: 110px;
    float: right;
}

.paging {
    text-align: center;
    margin-bottom: 10px;
    margin-top: 10px;
}

.paging span {
    font-size: 12px;
    font-weight: bold;
    color: #<%= borderColor %>;
    margin: 0px 3px 0px 3px;
}

.paging a {
    font-size: 12px;
    font-weight: bold;

    margin: 0px 3px 0px 3px;
    text-decoration: none;

    color: #<%= borderColor %>;
}

.paging a:hover {
    text-decoration: underline;
}

.paging_arrow {
    font-size: 10px !important;
    margin-left: 5px !important;
    margin-right: 5px !important;
}

.paging_actual_page {
    font-size: 22px !important;
}

/* /PAGING */

/* DIALOGS */
.ui-widget-overlay {
    background: #666666 url(../images/ui-bg_diagonals-thick_20_666666_40x40.png) 50% 50% repeat;
    opacity: .50;
    filter:Alpha(Opacity=50);
    position: absolute;
    left: 0;
    top: 0;
}

.ui-widget {
    background-color: #<%= backgroundLightColor %>;
    border: 1px solid #<%= borderColor %>;
}

.ui-widget fieldset {
    border: 0px;
}

.ui-dialog {
    position: absolute;
    padding: 10px 5px 10px 5px;
    width: 300px;
    overflow: hidden;
}

.ui-dialog .ui-dialog-titlebar {
    display: none;
}

/* /DIALOGS */

/* SEARCH */

.search_box {
    text-align: left;
}

.search_box table {
    margin-top: 5px;
    width: 100%;
}

.search_box table td {
    font-size: 13px;
    font-weight: bold;
    vertical-align: middle;
}

.search_box table .search_term td {
    padding-top: 3px;
}

.search_box table .search_term_b td {
    padding-top: 6px;
}

.search_box table .biblio_search_term td {
    padding-bottom: 10px;
}

.search_box td * {
    vertical-align: middle;
}

.search_box .label_1_acquisition {
    text-align: right;
    width: 33%;
}

.search_box .label_2_acquisition {
    padding: 0px 10px 0px 5px;
}

.search_box .label_3_acquisition {
    white-space: nowrap;
    width: 40%;
}

.search_box .label_1_distributed {
    text-align: right;
    width: 25%;
}

.search_box .label_2_distributed {
    padding: 0px 10px 0px 5px;
}

.search_box .label_3_distributed {
    white-space: nowrap;
    width: 40%;
}


.search_box .label_1_biblio {
    text-align: right;
    width: 33%;
}

.search_box .label_2_biblio {
    padding: 0px 10px 0px 5px;
}

.search_box .label_3_biblio {
    white-space: nowrap;
    width: 40%;
}

.search_box .label_1_user {
    text-align: right;
    width: 12%;
}

.search_box .label_2_user {
    padding: 0px 10px 0px 5px;
}

.search_box .label_3_user {
    width: 12%;
}

.search_box .label_1_card {
    text-align: right;
    width: 25%;
}

.search_box .label_2_card {
    padding: 0px 10px 0px 5px;
}

.search_box .label_3_card {
    width: 25%;
}

.search_box .label_1_holding {
    text-align: right;
    width: 25%;
}

.search_box .label_2_holding {
    padding: 0px 10px 0px 5px;
}

.search_box .label_3_holding {
    width: 25%;
}

.search_box .label_1 {
    text-align: right;
    width: 20%;
}

.search_box .label_2 {
    padding: 0px 10px 0px 5px;
}

.search_box .label_3 {
    width: 20%;
}

.search_box .input_term {
    width: 100%;
}

.search_box .input_material {
    width: 100%;
}


.search_box .biblio_input_term {
    width: 98%;
}

.search_box .input_user_enrol {
    width: 30%;
}

.search_box .search_list_all {
    float: left;
}

.search_box .search_add {
    text-align: center;
}

.search_box .search_clear {
    text-align: center;
}

.search_box .search_submit {
    float: right;
}

.search_print {
    margin-bottom: 10px;
    text-align: center;
    display: none;
}

.search_box .form_buttons td {
    padding-top: 10px;
}

.search_box .form_buttons button {
    width: 150px;
}

.submit_buttons {
    text-align: center;
}

.submit_buttons button {
    min-width: 160px;
    margin: 0 10px 0 10px;
    white-space: nowrapç
}

.ie .submit_buttons button {
    width: 160px;
}








#login_page_inner {
    padding: 10px 15px 10px 15px;
    text-align: justify;
    line-height: 20px;
}

#logged_page_inner {
    padding: 20px 15px 10px 15px;
    text-align: center;
}

/** BUSCA BIBLIOGRAFICA  **/
#biblio_search_box {
}

#biblio_search_term_template {
    display: none;
}
/** /BUSCA BIBLIOGRAFICA */

.biblio_record_table {
    width: 100%;
    margin-top: 15px;
}

.biblio_record_table_col1 {
    font-weight: bold;
    text-align: right;
    padding-right: 10px;
    white-space: nowrap;
    width: 20%;
    font-size: 12px;
    line-height: 18px;
    padding-bottom: 5px;
}

.biblio_record_table_col2 {
    font-size: 12px;
    line-height: 18px;
    padding-bottom: 5px;
}


.biblio_marc_table {
    margin-top: 10px;
    width: 100%;
}

.biblio_marc_table_col1 {
    font-weight: bold;
    text-align: right;
    padding-right: 10px;
    white-space: nowrap;
    width: 15%;
    font-size: 14px;
    line-height: 18px;
}

.biblio_marc_table_col2 {
    font-size: 14px;
    line-height: 18px;
    padding-bottom: 5px;
}

#auth_search_box {
}

.auth_record_table {
    width: 100%;
    margin-top: 15px;
}

.auth_record_table_col1 {
    font-weight: bold;
    text-align: right;
    padding-right: 10px;
    white-space: nowrap;
    width: 20%;
    font-size: 12px;
    line-height: 18px;
}

.auth_record_table_col2 {
    font-size: 12px;
    line-height: 18px;
    padding-bottom: 5px;
}

.auth_search_print {
    margin-bottom: 10px;
    text-align: center;
    display: none;
}

.auth_marc_table {
    width: 100%;
    margin-top: 15px;
}

.auth_marc_table_col1 {
    font-weight: bold;
    text-align: right;
    padding-right: 10px;
    white-space: nowrap;
    width: 15%;
    font-size: 14px;
    line-height: 18px;
}

.auth_marc_table_col2 {
    font-size: 14px;
    line-height: 18px;
    padding-bottom: 5px;
}

.biblio_database {
    font-weight: bold;
    font-size: 14px;
    text-align: center;
    line-height: 18px;
}

.import_combo {
    font-weight: bold;
    font-size: 14px;
    text-align: center;
    line-height: 18px;
}

.userPhoto {
    border: 1px solid #000000;
    width: 75px;
    height: 100px;
}

.userBigPhoto {
    border: 1px solid #000000;
    width: 150px;
    height: 200px;
}

.tab_inner_title {
    margin-top: 5px;
}

.tab_inner_links {
    text-align: center;
    font-size: 14px;
    font-weight: bold;
    line-height: 18px;
}

.tab_inner_availability {
    text-align: center;
    font-size: 12px;
    margin-top: 10px;
}


.registerTable {
    width: 100%;
    margin: 10px 0px 10px 0px;
}

.registerTable td {
    padding: 5px !important;
    font-size: 12px;
    line-height: 18px;
    vertical-align: top;
}

.registerTable input {
    width: 98%;
}

.registerTable textarea {
    width: 99%;
    height: 100px;
}


.ui-effects-transfer {
    border: 1px dashed #<%= borderColor %>;
}

.z3950_server_list {
    width: 500px;
    margin: 10px auto 10px auto;
    font-weight: normal;
}

#export_box {
    display: none;
    margin-top: 5px;
    font-size: 12px;
    padding: 10px;
    line-height: 16px;
}

#export_box .export_item {
    margin: 5px 10px 5px 10px;
}

#export_box .export_item a {
    font-size: smaller;
    font-weight: bold;
}

.database_MAIN .move_to_main {
    display: none;
}

.database_WORK .move_to_work {
    display: none;
}

.last_backup {
    padding: 2px;
    font-weight: bold;
    font-size: 16px;
    line-height: 30px;
}

.red {
    color: red;
}


.ac_results {
	padding: 0px;
	border: 1px solid black;
	background-color: white;
	overflow: hidden;
	z-index: 99999;
}

.ac_results ul {
	width: 100%;
	list-style-position: outside;
	list-style: none;
	padding: 0;
	margin: 0;
}

.ac_results li {
	margin: 0px;
	padding: 2px 5px;
	cursor: default;
	display: block;
	/*
	if width will be 100% horizontal scrollbar will apear
	when scroll mode will be used
	*/
	/*width: 100%;*/
	font: menu;
	font-size: 12px;
	/*
	it is very important, if line-height not setted or setted
	in relative units scroll will be broken in firefox
	*/
	line-height: 16px;
	overflow: hidden;
}

.ac_loading {
	background: white url('indicator.gif') right center no-repeat;
}

.ac_odd {
	background-color: #eee;
}

.ac_over {
	background-color: #0A246A;
	color: white;
}
