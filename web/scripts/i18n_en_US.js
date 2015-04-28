var Translations = {
    SHORT_DATE: 'MM/dd/yyyy',
    SHORT_DATETIME: 'MM/dd/yyyy hh:mm',
    LOADING: "Loading...",
    SHOW_MORE: function(n) {
        return n ? "Show all the others" + n + "..." : "Show all...";
    },
    RECORDS_FOUND: function(n) {
        return (n == 1) ? "records found" : "records found";
    },
    USERS_FOUND: function(n) {
        return (n == 1) ? "users found" : "users found";
    },
    AUTHORS_FOUND: function(n) {
        return (n == 1) ? "author found": "authors found";
    },
    USER_STATUS: {
        ACTIVE: 'Active',
        PENDINGS: 'Pending',
        BLOCKED: 'Blocked'
    },
    CARD_STATUS: {
        AVAILABLE: 'Available',
        IN_USE: 'In use',
        BLOCKED: 'Blocked',
        IN_USE_AND_BLOCKED: 'In use and blocked',
        CANCELLED: 'Cancelled'
    },
    ERROR_FIRST_RECORD: "This is the first record",
    ERROR_LAST_RECORD: "This is the last record",
    ERROR_SEARCH_FIRST: "You must search before performing this action",
    ERROR_TYPE_ALL_SEARCH_PARAMS: "Fill all search fields",
    HOLDING_FOUND: "Holding found",
    CARD_FOUND: "Card found",
    CONFIRM_USER_REPORT: function(userName) {
        return "Do you wish to create user's report for " + userName + "?";
    },
    CONFIRM_AUTHOR_REPORT: function(name) {
        return "Do you wish to create author's report for " + name + "?";
    },
    CONFIRM_DELETE_CARD: function(cardNumber) {
        return "Do you really wish to delete the card " + cardNumber + "?";
    },
    ERROR_EMPTY_PASSWORD: "The password cannot be empty",
    ERROR_PASSWORDS_DONT_MATCH: "The passwords don't match ",
    ERROR_EMPTY_LOGINNAME: "The login field is obligatory",
    ERROR_EMPTY_RECEIPT_LIST: "It is not possible to create a receipt because no operation has been carried out",
    CONFIRM_CANCEL_AUTH: "Do you wish to cancel the authority editing?",
    CONFIRM_CANCEL_BIBLIO: "Do you wish to cancel the bibliographic record editing?",
    CONFIRM_CANCEL_HOLDING: "Do you wish to cancel the copy editing?",
    CONFIRM_CANCEL: "Do you wish to cancel the editing?",
    REPEAT: "Repeat",
    REMOVE: "Remove",
    CONFIRM_DELETE: "Do you really wish to delete this record?",
    FILE_UPLOAD_ERROR: "Error on sending file, check the DigitalMediaServer",
    IMPORT_FILE_UPLOAD_ERROR: "Error on sending file, check its content ",
    LABEL_ADDED_TO_QUEUE: "Label added to queue ",
    EXPORT_ADDED_TO_QUEUE: "Added to queue",
    USER_CARD_ADDED_TO_QUEUE: "Added to queue",
    FILE_DESCRIPTION: "You did not fill out the description of the file. Fill it out now:",
    ERROR_CREATE_AT_LEAST_ONE_HOLDING: " To create copies automatically, it is necessary to enter a complete positive value in the field Number of copies.",
    ERROR_FINE_MUST_HAVE_NUMERIC_VALUE: "To create a fine, you must use a numeric value.",
    ERROR_RETURN_IS_LATE_SO_USER_CANT_RENEW: "This return is late and cannot be renewed. Make a return first.",
    ERROR_EXPIRED_SESSION: "Your session has expired. Repeat a login to continue.",
    ERROR_REQUEST_VALUES_MUST_BE_POSITIVE_NUMBERS: "The number of copies and the unit value must be positive numbers",
    ERROR_REQUEST_ALREADY_IN_QUEUE: "This request is already in the queue below",


    CONFIRM_DELETE_LOGIN: "Do you really want to delete this user’s login?\n\n This will delete all his permissions",
    CONFIRM_MOVE_FROM_WORK_DATABASE: "Do you really want to move all records of the selected type of material to the main database?",
    CONFIRM_MOVE_FROM_MAIN_DATABASE: "Do you really want to move all the selected type of material to the work database?",
    NO_HOLDINGS_AVAILABLE_WANT_FUTURE_RESERVATION: "This record has no copies available at the moment.\n\n Do you still want to make a reservation?",
    NO_HOLDINGS_LENT_OR_RESERVED: "This record has no copies on loan or reserved.",
    LABEL_RESERVATIONS: "Reservations",
    LABEL_LENDINGS: "Loans",
    LABEL_NAME: "Name",
    LABEL_RESERVATION_DATE: "Date of reservation",
    LABEL_RESERVATION_EXPIRATION_DATE: "Expiration date",
    LABEL_LENDING_DATE: "Lending date",
    LABEL_LENDING_EXPIRATION_DATE: "Lending expiration date",
    
    LABEL_FORMAT: function(label) {
        return label.paper_size + " " + (label.columns * label.rows) + " stickers (" + label.height + " mm x " + label.width + " mm)";
    }
};
