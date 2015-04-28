var Reservation = {};

Reservation.tabHandler = function(tab, params) {
    var fromTab = $('div.tab_selected').attr('rel');

    // dont change if we are changing to the same tab
    if (tab == fromTab) {
        return false;
    }

    if (tab == 'records') {
        $('#user_selected').checkEmptyTemplate();
        Bibliographic.searchClearResults();

        if (params) {
            $('#user_reserved_books').resetTemplate();

            Circulation.selectUser(params, function() {
                $('#user_reservations').show();
                Reservation.loadUserReservations(params);
            });

        }
    } else if (tab == 'list') {
        Reservation.listAll();
    }

    return true;
};

Reservation.listAll = function() {
    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'reservation',
            submitButton: 'list_all_reservations'
        },
        //loadingHolder: 'div.user_selected_box h1:first',
        success: function(data) {
            if (data.success) {
                $('#reserve_results').resetTemplate();

                Reservation.reservationsDumpResults({
                    data: data,
                    holder: '#reserve_results',
                    limit: 10
                });
            } else {
                $.msg({
                    cls: data.errorLevel,
                    msg: data.message
                })
            }
        }
    });

};

Reservation.loadUserReservations = function(userId) {
    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'reservation',
            submitButton: 'list_reservations',
            user_id: userId
        },
        //loadingHolder: 'div.user_selected_box h1:first',
        success: function(data) {
            if (data.success) {
                Reservation.reservationsDumpResults({
                    data: data
                });
            } else {
                $.msg({
                    cls: data.errorLevel,
                    msg: data.message
                })
            }
        }
    });
};

Reservation.reservationsDumpResults = function(o) {
    var config = $.extend({}, {
        holder: '#user_reserved_books'
    }, o);

    if (!config.data) {
        return;
    }

    var holder = $(config.holder);
    if (!holder) {
        return;
    }

    $.applyTemplates({
        holder: holder,
        array: config.data.data,
        stripe: true,
        limit: config.limit
    });

    holder.find('button').removeAttr('disabled');
};

Reservation.deleteReservation = function(reservation_id, button) {
    var params = {
        thisPage: 'reservation',
        submitButton: 'delete_reserve',
        reservation_id: reservation_id
    };

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: params,
        success: function(data) {
            if (data.success) {
                $(button).parents('.box').fadeOut('normal', function() {
                    $(this).remove();
                    $('#user_reserved_books').checkEmptyTemplate();
                });
            }

            $.msg({
                cls: data.errorLevel,
                msg: data.message,
                animate: !data.success
            });
        }
    });
};

Reservation.reserve = function(button, recordSerial) {
    var result = $(button).parents('.box').parent().data('result');
    var available = 0;
    if (result) {
        available = result.holdings_available;
    }

    if (available < 1) {
        if (!confirm(Translations.NO_HOLDINGS_AVAILABLE_WANT_FUTURE_RESERVATION)) {
            return;
        }
    }
    
    var params = {
        thisPage: 'reservation',
        submitButton: 'reserve_record',
        record_serial: recordSerial,
        user_id: $('#user_selected').data('lastUserId')
    };

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: params,
        success: function(data) {
            if (data.success) {
                var holder = $('#user_reserved_books');

                $.applyTemplates({
                    holder: holder,
                    object: data,
                    prepend: true
                });

                Bibliographic.searchClearResults();

                $.msg({
                    msg: data.message
                });
            } else {
                $.msg({
                    cls: data.errorLevel,
                    msg: data.message
                });
            }
        }
    });
};

Reservation.listCirculations = function(button, record_serial) {
    var result = $(button).parents('.box').parent().data('result');
    var lent = 0;
    var reserved = 0;
    if (result) {
        lent = result.holdings_lent;
        reserved = result.holdings_reserved;
    }

    if (lent + reserved == 0) {
        alert(Translations.NO_HOLDINGS_LENT_OR_RESERVED);
        return;
    }
    
    var holder = $(button).parents('.box').find('.current_circulations');

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: {
            thisPage: 'reservation',
            submitButton: 'list_pending_circulations',
            record_serial: record_serial
        },
        loadingHolder: holder,
        success: function(data) {
            if (data.success) {
                holder.empty();

                if (data.reservations && data.reservations.data && data.reservations.data.length) {
                    holder.append('<br/><b>' + Translations.LABEL_RESERVATIONS + '</b><br/><br/>');
                    for (var i = 0; i < data.reservations.data.length; i++) {
                        var reservation = data.reservations.data[i];

                        holder.append('<b>' + Translations.LABEL_NAME + ':</b> ' + reservation.userName + '<br/>');
                        holder.append('<b>' + Translations.LABEL_RESERVATION_DATE + ':</b> ' + Core.formatDateDime(reservation.created) + '<br/>');
                        holder.append('<b>' + Translations.LABEL_RESERVATION_EXPIRATION_DATE + ':</b> ' + Core.formatDateDime(reservation.expires) + '<br/>');
                        holder.append('<br/>');
                    }
                }

                if (data.lendings && data.lendings.data && data.lendings.data.length) {
                    holder.append('<br/><b>' + Translations.LABEL_LENDINGS + '</b><br/><br/>');

                    for (var i = 0; i < data.lendings.data.length; i++) {
                        var lending = data.lendings.data[i];

                        holder.append('<b>' + Translations.LABEL_NAME + ':</b> ' + lending.userName + '<br/>');
                        holder.append('<b>' + Translations.LABEL_LENDING_DATE + ':</b> ' + Core.formatDateDime(lending.lendDate) + '<br/>');
                        holder.append('<b>' + Translations.LABEL_LENDING_EXPIRATION_DATE + ':</b> ' + Core.formatDateDime(lending.returnDate) + '<br/>');
                        holder.append('<br/>');
                    }
                }
            } else {
                $.msg({
                    cls: data.errorLevel,
                    msg: data.message
                });
            }
        }
    });
};