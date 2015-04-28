var Translations = {
    SHORT_DATE: 'dd/MM/yyyy',
    SHORT_DATETIME: 'dd/MM/yyyy hh:mm',
    LOADING: "Cargando...",
    SHOW_MORE: function(n) {
        return n ? "Exhibir todos los otros " + n + "..." : "Exhibir todos...";
    },
    RECORDS_FOUND: function(n) {
        return (n == 1) ? "registro encontrado" : "registros encontrados";
    },
    USERS_FOUND: function(n) {
        return (n == 1) ? "usuario encontrado" : "usuarios encontrados";
    },
    AUTHORS_FOUND: function(n) {
        return (n == 1) ? "autor encontrado" : "autores encontrados";
    },
    USER_STATUS: {
        ACTIVE: 'Activo',
        PENDINGS: 'Tiene pendências',
        BLOCKED: 'Bloquiado'
    },
    CARD_STATUS: {
        AVAILABLE: 'Disponible',
        IN_USE: 'En uso',
        BLOCKED: 'Bloquiado',
        IN_USE_AND_BLOCKED: 'En uso y bloquiado',
        CANCELLED: 'Cancelado'
    },
    ERROR_FIRST_RECORD: "Este es el primer registro",
    ERROR_LAST_RECORD: "Este es el último registro",
    ERROR_SEARCH_FIRST: "Buscar antes de realizar esta operación",
    ERROR_TYPE_ALL_SEARCH_PARAMS: "Por favor, complete todos los campos de búsqueda",
    HOLDING_FOUND: "Exemplar encontrado",
    CARD_FOUND: "Tarjeta encontrado",
    CONFIRM_USER_REPORT: function(userName) {
        return "Desea generar el relatorio del usuario " + userName + "?";
    },
    CONFIRM_AUTHOR_REPORT: function(name) {
        return "Desea generar el relatorio del autor " + name + "?";
    },
    CONFIRM_DELETE_CARD: function(cardNumber) {
        return "Deseja realmente deletar o cartão " + cardNumber + "?";
    },
    ERROR_EMPTY_PASSWORD: "A seña no puede ser esvaziada",
    ERROR_PASSWORDS_DONT_MATCH: "las señas no coinciden",
    ERROR_EMPTY_LOGINNAME: "El campo login es obligatorio",
    ERROR_EMPTY_RECEIPT_LIST: "No es posible generar un recibo porque  ninguna operación fue realizada",
    CONFIRM_CANCEL_AUTH: "Desea cancelar la edición de la autoridad?",
    CONFIRM_CANCEL_BIBLIO: "Desea cancelar la edición?",
    CONFIRM_CANCEL_HOLDING: "Desea cancelar la edición?",
    CONFIRM_CANCEL: "Desea cancelar la edición?",
    REPEAT: "Repetir",
    REMOVE: "Remover",
    CONFIRM_DELETE: "Desea realmente excluir el registro?",
    FILE_UPLOAD_ERROR: "Error al enviar arquivo, verifique el DigitalMediaServer",
    IMPORT_FILE_UPLOAD_ERROR: "Error  al enviar arquivo, verifique su contenido",
    LABEL_ADDED_TO_QUEUE: "Etiqueta adicionada a la lista",
    EXPORT_ADDED_TO_QUEUE: "Adicionado a la lista",
    USER_CARD_ADDED_TO_QUEUE: "Adicionado a la lista",
    FILE_DESCRIPTION: "Usted no lleno la descrición del arquivo. Llene agora:",
    ERROR_CREATE_AT_LEAST_ONE_HOLDING: "Para criar ejemplares automaticamente es necesario digitar un valor intero positivo em el campo Número de ejemplares.",
    ERROR_FINE_MUST_HAVE_NUMERIC_VALUE: "Para crear tener um valor numérico.",
    ERROR_RETURN_IS_LATE_SO_USER_CANT_RENEW: "Esta devolución está atrasada y no puede ser renovada. Haga una devolución primero.",
    ERROR_EXPIRED_SESSION: "Su seccion expirou. Haga login nuevamente para continuar.",
    ERROR_REQUEST_VALUES_MUST_BE_POSITIVE_NUMBERS: "La cuantidade de ejxemplares y el valor unitario deven ser numeros positivos",
    ERROR_REQUEST_ALREADY_IN_QUEUE: "Esta requisición ya está na lista abajo",

    
    CONFIRM_DELETE_LOGIN: "¿Desea realmente retirar el login de este usuario?\n\nEsto retirará todas sus autorizaciones",
    CONFIRM_MOVE_FROM_WORK_DATABASE: "¿Desea realmente mover todos los registros del tipo de material seleccionado para la base principal?",
    CONFIRM_MOVE_FROM_MAIN_DATABASE: "¿Desea realmente mover todos los registros del tipo de material seleccionado para la base de trabajo?",
    NO_HOLDINGS_AVAILABLE_WANT_FUTURE_RESERVATION: "Este registro no posee ejemplares disponibles en este momento.\n\n ¿Aún así desea reservarlo?",
    NO_HOLDINGS_LENT_OR_RESERVED: "Este registro no posee ejemplares prestados ni reservados.",
    LABEL_RESERVATIONS: "Reservas",
    LABEL_LENDINGS: "Préstamos",
    LABEL_NAME: "Nombre",
    LABEL_RESERVATION_DATE: "Fecha de la reserva",
    LABEL_RESERVATION_EXPIRATION_DATE: "Fecha límite",
    LABEL_LENDING_DATE: "Fecha de préstamo",
    LABEL_LENDING_EXPIRATION_DATE: "Fecha prevista para devolución",
    
    LABEL_FORMAT: function(label) {
        return label.paper_size + " " + (label.columns * label.rows) + " etiquetas (" + label.height + " mm x " + label.width + " mm)";
    }

};
