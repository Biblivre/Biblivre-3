var Translations = {
    SHORT_DATE: 'dd/MM/yyyy',
    SHORT_DATETIME: 'dd/MM/yyyy hh:mm',
    LOADING: "Carregando...",
    SHOW_MORE: function(n) {
        return n ? "Exibir todos os outros " + n + "..." : "Exibir todos...";
    },
    RECORDS_FOUND: function(n) {
        return (n == 1) ? "registro encontrado" : "registros encontrados";
    },
    USERS_FOUND: function(n) {
        return (n == 1) ? "usuário encontrado" : "usuários encontrados";
    },
    AUTHORS_FOUND: function(n) {
        return (n == 1) ? "autor encontrado" : "autores encontrados";
    },
    USER_STATUS: {
        ACTIVE: 'Ativo',
        PENDINGS: 'Possui pendências',
        BLOCKED: 'Bloqueado'
    },
    CARD_STATUS: {
        AVAILABLE: 'Disponível',
        IN_USE: 'Em uso',
        BLOCKED: 'Bloqueado',
        IN_USE_AND_BLOCKED: 'Em uso e bloqueado',
        CANCELLED: 'Cancelado'
    },
    ERROR_FIRST_RECORD: "Este já é o primeiro registro",
    ERROR_LAST_RECORD: "Este é o último registro",
    ERROR_SEARCH_FIRST: "Faça uma busca antes de executar esta operação",
    ERROR_TYPE_ALL_SEARCH_PARAMS: "Preencha todos os campos da busca",
    HOLDING_FOUND: "Exemplar encontrado",
    CARD_FOUND: "Cartão encontrado",
    CONFIRM_USER_REPORT: function(userName) {
        return "Deseja gerar o relatório do usuário " + userName + "?";
    },
    CONFIRM_AUTHOR_REPORT: function(name) {
        return "Deseja gerar o relatório do autor " + name + "?";
    },
    CONFIRM_DELETE_CARD: function(cardNumber) {
        return "Deseja realmente deletar o cartão " + cardNumber + "?";
    },
    ERROR_EMPTY_PASSWORD: "A senha não pode ser vazia",
    ERROR_PASSWORDS_DONT_MATCH: "As senhas não conferem",
    ERROR_EMPTY_LOGINNAME: "O campo login é obrigatório",
    ERROR_EMPTY_RECEIPT_LIST: "Não é possível gerar um recibo pois nenhuma operação foi efetuada.",
    CONFIRM_CANCEL_AUTH: "Deseja cancelar a edição da autoridade?",
    CONFIRM_CANCEL_BIBLIO: "Deseja cancelar a edição do registro bibliográfico?",
    CONFIRM_CANCEL_HOLDING: "Deseja cancelar a edição do exemplar?",
    CONFIRM_CANCEL: "Deseja cancelar a edição?",
    REPEAT: "Repetir",
    REMOVE: "Remover",
    CONFIRM_DELETE: "Deseja realmente excluir este registro?",
    FILE_UPLOAD_ERROR: "Erro ao enviar arquivo, verifique o DigitalMediaServer",
    IMPORT_FILE_UPLOAD_ERROR: "Erro ao enviar arquivo, verifique seu conteúdo",
    LABEL_ADDED_TO_QUEUE: "Etiqueta adicionada à lista",
    EXPORT_ADDED_TO_QUEUE: "Adicionado à lista",
    USER_CARD_ADDED_TO_QUEUE: "Adicionado à lista",
    FILE_DESCRIPTION: "Você não preencheu a descrição do arquivo. Preencha agora:",
    ERROR_CREATE_AT_LEAST_ONE_HOLDING: "Para criar exemplares automaticamente é necessário digitar um valor inteiro positivo no campo Número de exemplares.",
    ERROR_FINE_MUST_HAVE_NUMERIC_VALUE: "Para criar uma multa ela deve possuir um valor numérico.",
    ERROR_RETURN_IS_LATE_SO_USER_CANT_RENEW: "Esta devolução está atrasada e não pode ser renovada. Faça uma devolução primeiro.",
    ERROR_EXPIRED_SESSION: "Sua sessão expirou. Faça login novamente para continuar.",
    ERROR_REQUEST_VALUES_MUST_BE_POSITIVE_NUMBERS: "A quantidade de exemplares e o valor unitário devem ser numeros positivos",
    ERROR_REQUEST_ALREADY_IN_QUEUE: "Esta requisição já está na lista abaixo",


    CONFIRM_DELETE_LOGIN: "Deseja realmente remover o login deste usuário?\n\nIsso removerá todas as suas permissões",
    CONFIRM_MOVE_FROM_WORK_DATABASE: "Deseja realmente mover todos os registros do tipo de material selecionado para a base principal?",
    CONFIRM_MOVE_FROM_MAIN_DATABASE: "Deseja realmente mover todos os registros do tipo de material selecionado para a base de trabalho?",
    NO_HOLDINGS_AVAILABLE_WANT_FUTURE_RESERVATION: "Este registro não possui exemplares disponíveis no momento.\n\nDeseja reservar mesmo assim?",
    NO_HOLDINGS_LENT_OR_RESERVED: "Este registro não possui exemplares emprestados nem reservados.",
    LABEL_RESERVATIONS: "Reservas",
    LABEL_LENDINGS: "Empréstimos",
    LABEL_NAME: "Nome",
    LABEL_RESERVATION_DATE: "Data da reserva",
    LABEL_RESERVATION_EXPIRATION_DATE: "Data limite",
    LABEL_LENDING_DATE: "Data de empréstimo",
    LABEL_LENDING_EXPIRATION_DATE: "Data prevista para devolução",
    
    LABEL_FORMAT: function(label) {
        return label.paper_size + " " + (label.columns * label.rows) + " etiquetas (" + label.height + " mm x " + label.width + " mm)";
    }
};