var Reports = {};

Reports.authorSearch = function(listAll) {
    var params = {};

    if (!listAll) {
        params = $('#author_search_box :input:enabled:not(button)').getFormValues();
    }

    params['thisPage'] = 'reports';
    params['submitButton'] = 'search_authors';

    Reports.authorSearchSubmit(params);
};

Reports.authorSearchSubmit = function(params) {

    Reports.clearAuthorSearchResults();
    $('#author_search_results').data('lastParams', params);

    $.ajax({
        url: 'JsonController',
        type: 'POST',
        dataType: 'json',
        data: params,
        loadingHolder: 'div.author_search_paging:first',
        success: function(data) {
            if (data.success) {
                // On success, dump the results to the page
                Circulation.dumpSearchResults({
                    holder: '#author_search_results',
                    pagingHolder: 'div.author_search_paging',
                    msgFunction: Translations.AUTHORS_FOUND,
                    params: params,
                    data: data
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

Reports.clearAuthorSearchResults = function() {
    $('#author_search_results').resetTemplate();
    $('div.author_search_paging').empty();
};