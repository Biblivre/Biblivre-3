/**
 *  Este arquivo é parte do Biblivre3.
 *  
 *  Biblivre3 é um software livre; você pode redistribuí-lo e/ou 
 *  modificá-lo dentro dos termos da Licença Pública Geral GNU como 
 *  publicada pela Fundação do Software Livre (FSF); na versão 3 da 
 *  Licença, ou (caso queira) qualquer versão posterior.
 *  
 *  Este programa é distribuído na esperança de que possa ser  útil, 
 *  mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 *  MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 *  Licença Pública Geral GNU para maiores detalhes.
 *  
 *  Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 *  com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 * 
 *  @author Alberto Wagner <alberto@biblivre.org.br>
 *  @author Danniel Willian <danniel@biblivre.org.br>
 * 
 */

package biblivre3.authorization;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Alberto Wagner
 */
public class AuthorizationPoints implements Serializable {

    private HashMap<String, AuthorizationPoint> points;
    private HashMap<String, Boolean> permissions;
    private boolean admin;

    public AuthorizationPoints(HashMap<String, Boolean> permissions) {
        this.admin = false;
        this.permissions = permissions;

        if (this.permissions == null) {
            this.permissions = new HashMap<String, Boolean>();
        }

        this.points = new HashMap<String, AuthorizationPoint>();

        //MENU
        this.addAuthPoint("*", "LOGIN", AuthorizationPointTypes.MENU_LOGIN);
        this.addAuthPoint("*", "LOGOUT", AuthorizationPointTypes.MENU_LOGIN);

        this.addAuthPoint("*", "ACQUISITION_ORDER", AuthorizationPointTypes.ACQUISITION_ORDER_LIST);
        this.addAuthPoint("*", "ACQUISITION_QUOTATION", AuthorizationPointTypes.ACQUISITION_QUOTATION_LIST);
        this.addAuthPoint("*", "ACQUISITION_REQUISITION", AuthorizationPointTypes.ACQUISITION_REQUEST_LIST);
        this.addAuthPoint("*", "ACQUISITION_SUPPLIER", AuthorizationPointTypes.ACQUISITION_SUPPLIER_LIST);

        this.addAuthPoint("*", "ADMINISTRATION_ACCESSCARDS", AuthorizationPointTypes.ACCESS_CARDS_LIST);
        this.addAuthPoint("*", "ADMINISTRATION_CONFIGURATION", AuthorizationPointTypes.ADMIN_CONFIG_SAVE);
        this.addAuthPoint("*", "ADMINISTRATION_USER_TYPES", AuthorizationPointTypes.ADMIN_USER_TYPE_LIST);

        this.addAuthPoint("*", "ADMINISTRATION_MAINTENANCE", AuthorizationPointTypes.ADMIN_BACKUP, AuthorizationPointTypes.ADMIN_REINDEX);
        this.addAuthPoint("*", "ADMINISTRATION_PASSWORD", AuthorizationPointTypes.ADMIN_CHANGE_PASSWORD);
        this.addAuthPoint("*", "ADMINISTRATION_PERMISSIONS", AuthorizationPointTypes.ADMIN_PERMISSIONS);
        this.addAuthPoint("*", "ADMINISTRATION_REPORTS", AuthorizationPointTypes.REPORT_CREATE);
        this.addAuthPoint("*", "ADMINISTRATION_Z3950SERVERS", AuthorizationPointTypes.Z3950_MANAGE_LOCAL_SERVER, AuthorizationPointTypes.Z3950_MANAGE_SERVERS);

        this.addAuthPoint("*", "CATALOGING_AUTH", AuthorizationPointTypes.CATALOGING_AUTH_SAVE, AuthorizationPointTypes.CATALOGING_AUTH_DELETE);
        this.addAuthPoint("*", "CATALOGING_BIBLIO", AuthorizationPointTypes.CATALOGING_BIBLIO_SAVE, AuthorizationPointTypes.CATALOGING_BIBLIO_DELETE);
        this.addAuthPoint("*", "CATALOGING_VOCABULARY", AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE, AuthorizationPointTypes.CATALOGING_VOCABULARY_DELETE);

        this.addAuthPoint("*", "CATALOGING_IMPORT", AuthorizationPointTypes.CATALOGING_AUTH_SAVE, AuthorizationPointTypes.CATALOGING_BIBLIO_SAVE, AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE);
        this.addAuthPoint("*", "CATALOGING_LABEL", AuthorizationPointTypes.CATALOGING_HOLDING_PRINT);
        this.addAuthPoint("*", "CATALOGING_BIBLIO_MOVE", AuthorizationPointTypes.CATALOGING_BIBLIO_MOVE);

        this.addAuthPoint("*", "CIRCULATION_ACCESS", AuthorizationPointTypes.CIRCULATION_CARD);
        this.addAuthPoint("*", "CIRCULATION_LENDING", AuthorizationPointTypes.CIRCULATION_LENDING_LIST);
        this.addAuthPoint("*", "CIRCULATION_REGISTER", AuthorizationPointTypes.CIRCULATION_USER_LIST);
        this.addAuthPoint("*", "CIRCULATION_RESERVATION", AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST);
        this.addAuthPoint("*", "CIRCULATION_USER_CARDS", AuthorizationPointTypes.CIRCULATION_USER_CARD);

        this.addAuthPoint("*", "HELP_ABOUT", AuthorizationPointTypes.MENU_HELP);
        this.addAuthPoint("*", "SEARCH_AUTH", AuthorizationPointTypes.MENU_SEARCH);
        this.addAuthPoint("*", "SEARCH_BIBLIO", AuthorizationPointTypes.MENU_SEARCH);
        this.addAuthPoint("*", "SEARCH_THESAURUS", AuthorizationPointTypes.MENU_SEARCH);
        this.addAuthPoint("*", "SEARCH_Z3950", AuthorizationPointTypes.MENU_SEARCH);


        //JSON
        this.addAuthPoint("biblivre3.acquisition.supplier.JsonSupplierHandler", "search", AuthorizationPointTypes.ACQUISITION_SUPPLIER_LIST);
        this.addAuthPoint("biblivre3.acquisition.supplier.JsonSupplierHandler", "open", AuthorizationPointTypes.ACQUISITION_SUPPLIER_LIST);
        this.addAuthPoint("biblivre3.acquisition.supplier.JsonSupplierHandler", "save", AuthorizationPointTypes.ACQUISITION_SUPPLIER_SAVE);
        this.addAuthPoint("biblivre3.acquisition.supplier.JsonSupplierHandler", "delete", AuthorizationPointTypes.ACQUISITION_SUPPLIER_DELETE);

        this.addAuthPoint("biblivre3.acquisition.request.JsonRequestHandler", "search", AuthorizationPointTypes.ACQUISITION_REQUEST_LIST);
        this.addAuthPoint("biblivre3.acquisition.request.JsonRequestHandler", "open", AuthorizationPointTypes.ACQUISITION_REQUEST_LIST);
        this.addAuthPoint("biblivre3.acquisition.request.JsonRequestHandler", "save", AuthorizationPointTypes.ACQUISITION_REQUEST_SAVE);
        this.addAuthPoint("biblivre3.acquisition.request.JsonRequestHandler", "delete", AuthorizationPointTypes.ACQUISITION_REQUEST_DELETE);

        this.addAuthPoint("biblivre3.acquisition.quotation.JsonQuotationHandler", "search", AuthorizationPointTypes.ACQUISITION_QUOTATION_LIST);
        this.addAuthPoint("biblivre3.acquisition.quotation.JsonQuotationHandler", "open", AuthorizationPointTypes.ACQUISITION_QUOTATION_LIST);
        this.addAuthPoint("biblivre3.acquisition.quotation.JsonQuotationHandler", "save", AuthorizationPointTypes.ACQUISITION_QUOTATION_SAVE);
        this.addAuthPoint("biblivre3.acquisition.quotation.JsonQuotationHandler", "delete", AuthorizationPointTypes.ACQUISITION_QUOTATION_DELETE);

        this.addAuthPoint("biblivre3.acquisition.order.JsonBuyOrderHandler", "search", AuthorizationPointTypes.ACQUISITION_ORDER_LIST);
        this.addAuthPoint("biblivre3.acquisition.order.JsonBuyOrderHandler", "open", AuthorizationPointTypes.ACQUISITION_ORDER_LIST);
        this.addAuthPoint("biblivre3.acquisition.order.JsonBuyOrderHandler", "save", AuthorizationPointTypes.ACQUISITION_ORDER_SAVE);
        this.addAuthPoint("biblivre3.acquisition.order.JsonBuyOrderHandler", "delete", AuthorizationPointTypes.ACQUISITION_ORDER_DELETE);

        this.addAuthPoint("biblivre3.administration.JsonReportsHandler", "search", AuthorizationPointTypes.REPORT_CREATE);
        this.addAuthPoint("biblivre3.administration.JsonReportsHandler", "search_authors", AuthorizationPointTypes.REPORT_CREATE);

        this.addAuthPoint("biblivre3.administration.cards.JsonCardHandler", "search_cards", AuthorizationPointTypes.ACCESS_CARDS_LIST);
        this.addAuthPoint("biblivre3.administration.cards.JsonCardHandler", "add_card", AuthorizationPointTypes.ACCESS_CARDS_SAVE);
        this.addAuthPoint("biblivre3.administration.cards.JsonCardHandler", "add_card_list", AuthorizationPointTypes.ACCESS_CARDS_SAVE);
        this.addAuthPoint("biblivre3.administration.cards.JsonCardHandler", "delete_card", AuthorizationPointTypes.ACCESS_CARDS_SAVE);
        this.addAuthPoint("biblivre3.administration.cards.JsonCardHandler", "block_card", AuthorizationPointTypes.ACCESS_CARDS_BLOCK);
        this.addAuthPoint("biblivre3.administration.cards.JsonCardHandler", "unblock_card", AuthorizationPointTypes.ACCESS_CARDS_BLOCK);

        this.addAuthPoint("biblivre3.administration.permission.JsonPermissionHandler", "open", AuthorizationPointTypes.ADMIN_PERMISSIONS);
        this.addAuthPoint("biblivre3.administration.permission.JsonPermissionHandler", "save", AuthorizationPointTypes.ADMIN_PERMISSIONS);
        this.addAuthPoint("biblivre3.administration.permission.JsonPermissionHandler", "remove", AuthorizationPointTypes.ADMIN_PERMISSIONS);

        this.addAuthPoint("biblivre3.cataloging.authorities.JsonAuthoritiesHandler", "search", AuthorizationPointTypes.CATALOGING_AUTH_LIST);
        this.addAuthPoint("biblivre3.cataloging.authorities.JsonAuthoritiesHandler", "auto_complete", AuthorizationPointTypes.CATALOGING_AUTH_LIST);
        this.addAuthPoint("biblivre3.cataloging.authorities.JsonAuthoritiesHandler", "open", AuthorizationPointTypes.CATALOGING_AUTH_LIST);
        this.addAuthPoint("biblivre3.cataloging.authorities.JsonAuthoritiesHandler", "switch", AuthorizationPointTypes.CATALOGING_AUTH_LIST);
        this.addAuthPoint("biblivre3.cataloging.authorities.JsonAuthoritiesHandler", "save", AuthorizationPointTypes.CATALOGING_AUTH_SAVE);
        this.addAuthPoint("biblivre3.cataloging.authorities.JsonAuthoritiesHandler", "delete", AuthorizationPointTypes.CATALOGING_AUTH_DELETE);

        this.addAuthPoint("biblivre3.cataloging.bibliographic.JsonBiblioHandler", "search", AuthorizationPointTypes.CATALOGING_BIBLIO_LIST);
        this.addAuthPoint("biblivre3.cataloging.bibliographic.JsonBiblioHandler", "open", AuthorizationPointTypes.CATALOGING_BIBLIO_LIST);
        this.addAuthPoint("biblivre3.cataloging.bibliographic.JsonBiblioHandler", "switch", AuthorizationPointTypes.CATALOGING_BIBLIO_LIST);
        this.addAuthPoint("biblivre3.cataloging.bibliographic.JsonBiblioHandler", "item_count", AuthorizationPointTypes.CATALOGING_BIBLIO_LIST);
        this.addAuthPoint("biblivre3.cataloging.bibliographic.JsonBiblioHandler", "file_upload", AuthorizationPointTypes.CATALOGING_BIBLIO_SAVE);
        this.addAuthPoint("biblivre3.cataloging.bibliographic.JsonBiblioHandler", "move_records", AuthorizationPointTypes.CATALOGING_BIBLIO_MOVE);
        this.addAuthPoint("biblivre3.cataloging.bibliographic.JsonBiblioHandler", "move_all_records", AuthorizationPointTypes.CATALOGING_BIBLIO_MOVE);
        this.addAuthPoint("biblivre3.cataloging.bibliographic.JsonBiblioHandler", "save", AuthorizationPointTypes.CATALOGING_BIBLIO_SAVE);
        this.addAuthPoint("biblivre3.cataloging.bibliographic.JsonBiblioHandler", "delete", AuthorizationPointTypes.CATALOGING_BIBLIO_DELETE);

        this.addAuthPoint("biblivre3.cataloging.holding.JsonHoldingHandler", "open", AuthorizationPointTypes.CATALOGING_HOLDING_LIST);
        this.addAuthPoint("biblivre3.cataloging.holding.JsonHoldingHandler", "switch", AuthorizationPointTypes.CATALOGING_HOLDING_LIST);
        this.addAuthPoint("biblivre3.cataloging.holding.JsonHoldingHandler", "save", AuthorizationPointTypes.CATALOGING_HOLDING_SAVE);
        this.addAuthPoint("biblivre3.cataloging.holding.JsonHoldingHandler", "get_next_location", AuthorizationPointTypes.CATALOGING_HOLDING_SAVE);
        this.addAuthPoint("biblivre3.cataloging.holding.JsonHoldingHandler", "create_automatic_holding", AuthorizationPointTypes.CATALOGING_HOLDING_SAVE);
        this.addAuthPoint("biblivre3.cataloging.holding.JsonHoldingHandler", "delete", AuthorizationPointTypes.CATALOGING_HOLDING_DELETE);
        this.addAuthPoint("biblivre3.cataloging.holding.JsonHoldingHandler", "generate_label", AuthorizationPointTypes.CATALOGING_HOLDING_PRINT);

        this.addAuthPoint("biblivre3.cataloging.vocabulary.JsonVocabularyHandler", "search", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
        this.addAuthPoint("biblivre3.cataloging.vocabulary.JsonVocabularyHandler", "auto_complete", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
        this.addAuthPoint("biblivre3.cataloging.vocabulary.JsonVocabularyHandler", "open", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
        this.addAuthPoint("biblivre3.cataloging.vocabulary.JsonVocabularyHandler", "switch", AuthorizationPointTypes.CATALOGING_VOCABULARY_LIST);
        this.addAuthPoint("biblivre3.cataloging.vocabulary.JsonVocabularyHandler", "save", AuthorizationPointTypes.CATALOGING_VOCABULARY_SAVE);
        this.addAuthPoint("biblivre3.cataloging.vocabulary.JsonVocabularyHandler", "delete", AuthorizationPointTypes.CATALOGING_VOCABULARY_DELETE);

        this.addAuthPoint("biblivre3.circulation.JsonCirculationHandler", "search", AuthorizationPointTypes.CIRCULATION_USER_LIST);
        this.addAuthPoint("biblivre3.circulation.JsonCirculationHandler", "record", AuthorizationPointTypes.CIRCULATION_USER_LIST);
        this.addAuthPoint("biblivre3.circulation.JsonCirculationHandler", "save_user", AuthorizationPointTypes.CIRCULATION_USER_SAVE);
        this.addAuthPoint("biblivre3.circulation.JsonCirculationHandler", "delete_user", AuthorizationPointTypes.CIRCULATION_USER_DELETE);
        this.addAuthPoint("biblivre3.circulation.JsonCirculationHandler", "user_history", AuthorizationPointTypes.CIRCULATION_USER_LIST);
        this.addAuthPoint("biblivre3.circulation.JsonCirculationHandler", "create_user_card", AuthorizationPointTypes.CIRCULATION_USER_CARD);
        this.addAuthPoint("biblivre3.circulation.JsonCirculationHandler", "block_user", AuthorizationPointTypes.CIRCULATION_USER_BLOCK);
        this.addAuthPoint("biblivre3.circulation.JsonCirculationHandler", "unblock_user", AuthorizationPointTypes.CIRCULATION_USER_BLOCK);

        this.addAuthPoint("biblivre3.circulation.access.JsonAccessHandler", "get_card", AuthorizationPointTypes.CIRCULATION_CARD);
        this.addAuthPoint("biblivre3.circulation.access.JsonAccessHandler", "lend", AuthorizationPointTypes.CIRCULATION_CARD);
        this.addAuthPoint("biblivre3.circulation.access.JsonAccessHandler", "return", AuthorizationPointTypes.CIRCULATION_CARD);

        this.addAuthPoint("biblivre3.circulation.lending.JsonLendingHandler", "search", AuthorizationPointTypes.CIRCULATION_LENDING_LIST);
        this.addAuthPoint("biblivre3.circulation.lending.JsonLendingHandler", "list_lent", AuthorizationPointTypes.CIRCULATION_LENDING_LIST);
        this.addAuthPoint("biblivre3.circulation.lending.JsonLendingHandler", "list_all_lendings", AuthorizationPointTypes.CIRCULATION_LENDING_LIST);
        this.addAuthPoint("biblivre3.circulation.lending.JsonLendingHandler", "lend", AuthorizationPointTypes.CIRCULATION_LENDING_LEND);
        this.addAuthPoint("biblivre3.circulation.lending.JsonLendingHandler", "renew", AuthorizationPointTypes.CIRCULATION_LENDING_LEND);
        this.addAuthPoint("biblivre3.circulation.lending.JsonLendingHandler", "return", AuthorizationPointTypes.CIRCULATION_LENDING_LEND);
        this.addAuthPoint("biblivre3.circulation.lending.JsonLendingHandler", "pay_fine", AuthorizationPointTypes.CIRCULATION_LENDING_LEND);

        this.addAuthPoint("biblivre3.circulation.reservation.JsonReservationHandler", "search", AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST);
        this.addAuthPoint("biblivre3.circulation.reservation.JsonReservationHandler", "list_reservations", AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST);
        this.addAuthPoint("biblivre3.circulation.reservation.JsonReservationHandler", "list_all_reservations", AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST);
        this.addAuthPoint("biblivre3.circulation.reservation.JsonReservationHandler", "list_pending_circulations", AuthorizationPointTypes.CIRCULATION_RESERVATION_LIST);
        this.addAuthPoint("biblivre3.circulation.reservation.JsonReservationHandler", "reserve_record", AuthorizationPointTypes.CIRCULATION_RESERVATION_RESERVE);
        this.addAuthPoint("biblivre3.circulation.reservation.JsonReservationHandler", "delete_reserve", AuthorizationPointTypes.CIRCULATION_RESERVATION_RESERVE);

        this.addAuthPoint("biblivre3.circulation.UserCardsHandler", "DELETE_USER_CARD", AuthorizationPointTypes.CIRCULATION_USER_CARD);
        this.addAuthPoint("biblivre3.circulation.UserCardsHandler", "DOWNLOAD_USER_CARDS_FILE", AuthorizationPointTypes.CIRCULATION_USER_CARD);
        this.addAuthPoint("biblivre3.circulation.UserCardsHandler", "GENERATE_USER_CARDS_DATE", AuthorizationPointTypes.CIRCULATION_USER_CARD);
        this.addAuthPoint("biblivre3.circulation.UserCardsHandler", "LIST_ALL_PENDING_USER_CARDS", AuthorizationPointTypes.CIRCULATION_USER_CARD);
        this.addAuthPoint("biblivre3.circulation.UserCardsHandler", "RECORD_FILE_PDF", AuthorizationPointTypes.CIRCULATION_USER_CARD);

        this.addAuthPoint("biblivre3.z3950.JsonZ3950Handler", "search", AuthorizationPointTypes.CATALOGING_BIBLIO_LIST);
        this.addAuthPoint("biblivre3.z3950.JsonZ3950Handler", "paginate", AuthorizationPointTypes.CATALOGING_BIBLIO_LIST);
        this.addAuthPoint("biblivre3.z3950.JsonZ3950Handler", "open", AuthorizationPointTypes.CATALOGING_BIBLIO_LIST);
        this.addAuthPoint("biblivre3.z3950.JsonZ3950Handler", "save", AuthorizationPointTypes.CATALOGING_BIBLIO_SAVE);
        this.addAuthPoint("biblivre3.z3950.JsonZ3950Handler", "change_server_status", AuthorizationPointTypes.Z3950_MANAGE_LOCAL_SERVER);
        this.addAuthPoint("biblivre3.z3950.JsonZ3950Handler", "list_servers", AuthorizationPointTypes.Z3950_MANAGE_SERVERS);
        this.addAuthPoint("biblivre3.z3950.JsonZ3950Handler", "save_server", AuthorizationPointTypes.Z3950_MANAGE_SERVERS);
        this.addAuthPoint("biblivre3.z3950.JsonZ3950Handler", "delete_server", AuthorizationPointTypes.Z3950_MANAGE_SERVERS);
        this.addAuthPoint("biblivre3.z3950.JsonZ3950Handler", "change_server_status", AuthorizationPointTypes.Z3950_MANAGE_SERVERS);

        this.addAuthPoint("biblivre3.administration.JsonUserTypeHandler", "list", AuthorizationPointTypes.ADMIN_USER_TYPE_LIST);
        this.addAuthPoint("biblivre3.administration.JsonUserTypeHandler", "save", AuthorizationPointTypes.ADMIN_USER_TYPE_SAVE);
        this.addAuthPoint("biblivre3.administration.JsonUserTypeHandler", "delete", AuthorizationPointTypes.ADMIN_USER_TYPE_DELETE);

        //SERVLETS
        this.addAuthPoint("biblivre3.administration.AdminHandler", "CHANGE_PASSWORD", AuthorizationPointTypes.ADMIN_CHANGE_PASSWORD);
        this.addAuthPoint("biblivre3.administration.AdminHandler", "BACKUP", AuthorizationPointTypes.ADMIN_BACKUP);
        this.addAuthPoint("biblivre3.administration.AdminHandler", "REINDEX_BIBLIO_BASE", AuthorizationPointTypes.ADMIN_REINDEX);
        this.addAuthPoint("biblivre3.administration.AdminHandler", "REINDEX_AUTHORITIES_BASE", AuthorizationPointTypes.ADMIN_REINDEX);
        this.addAuthPoint("biblivre3.administration.AdminHandler", "REINDEX_THESAURUS_BASE", AuthorizationPointTypes.ADMIN_REINDEX);
        this.addAuthPoint("biblivre3.administration.AdminHandler", "EXPORT_ALL", AuthorizationPointTypes.ADMIN_BACKUP);
        
        this.addAuthPoint("biblivre3.administration.ConfigurationAdministrationHandler", "SAVE_CHANGES", AuthorizationPointTypes.ADMIN_CONFIG_SAVE);
        this.addAuthPoint("biblivre3.administration.ConfigurationAdministrationHandler", "CANCEL_CHANGES", AuthorizationPointTypes.ADMIN_CONFIG_SAVE);

        this.addAuthPoint("biblivre3.administration.ReportsHandler", "GENERATE_REPORT", AuthorizationPointTypes.REPORT_CREATE);

        this.addAuthPoint("biblivre3.cataloging.CatalogingHandler", "EXPORT_RECORD", AuthorizationPointTypes.CATALOGING_BIBLIO_LIST);
        this.addAuthPoint("biblivre3.cataloging.CatalogingHandler", "SAVE_IMPORT", AuthorizationPointTypes.CATALOGING_BIBLIO_SAVE);
        this.addAuthPoint("biblivre3.cataloging.CatalogingHandler", "UPLOAD_IMPORT", AuthorizationPointTypes.CATALOGING_BIBLIO_SAVE);

        this.addAuthPoint("biblivre3.cataloging.bibliographic.BiblioHandler", "DELETE_LABEL", AuthorizationPointTypes.CATALOGING_HOLDING_PRINT);
        this.addAuthPoint("biblivre3.cataloging.bibliographic.BiblioHandler", "DOWNLOAD_LABEL_FILE", AuthorizationPointTypes.CATALOGING_HOLDING_PRINT);
        this.addAuthPoint("biblivre3.cataloging.bibliographic.BiblioHandler", "GENERATE_LABELS_DATE", AuthorizationPointTypes.CATALOGING_HOLDING_PRINT);
        this.addAuthPoint("biblivre3.cataloging.bibliographic.BiblioHandler", "LIST_ALL_PENDING_LABELS", AuthorizationPointTypes.CATALOGING_HOLDING_PRINT);
        this.addAuthPoint("biblivre3.cataloging.bibliographic.BiblioHandler", "RECORD_FILE_TXT", AuthorizationPointTypes.CATALOGING_HOLDING_PRINT);
        this.addAuthPoint("biblivre3.cataloging.bibliographic.BiblioHandler", "RECORD_FILE_PDF", AuthorizationPointTypes.CATALOGING_HOLDING_PRINT);

        this.addAuthPoint("biblivre3.circulation.lending.LendingHandler", "LENDING_RECEIPT", AuthorizationPointTypes.CIRCULATION_LENDING_LEND);
    }

    private void addAuthPoint(String handler, String submitButton, AuthorizationPointTypes ... types) {
        AuthorizationPoint point = null;

        try {
            if (!handler.equals("*")) {
                Class.forName(handler);
            }
        } catch (ClassNotFoundException e) {
            return;
        }

        for (AuthorizationPointTypes type : types) {
            point = new AuthorizationPoint(handler, submitButton, type, type.isAlwaysAllowed() ? true : this.permissions.containsKey(type.toString()));

            if (point.isAllowed()) {
                break;
            }
        }

        if (point != null) {
            this.points.put(handler + ":" + submitButton, point);
        }
    }

    public boolean isAllowed(String handler, String submitButton) {
        AuthorizationPoint point = this.points.get("*:" + submitButton);

        if (point != null && (this.admin || point.isAllowed())) {
            return true;
        }

        point = this.points.get(handler + ":" + submitButton);

        if (point == null) {
            System.out.println("Action not found: " + handler + ":" + submitButton);
            return false;
        }

        if (point != null && (this.admin || point.isAllowed())) {
            return true;
        }

        return false;
    }

    public boolean isAllowed(AuthorizationPointTypes type) {
        if (this.admin) {
            return true;
        }

        return this.permissions.containsKey(type.toString());
    }

    public boolean menuAllowed(String submitButton) {
        if (this.admin) {
            return true;
        }

        AuthorizationPoint point = this.points.get("*:" + submitButton);

        if (point != null && point.isAllowed()) {
            return true;
        }

        return false;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAdmin() {
        return this.admin;
    }
}
