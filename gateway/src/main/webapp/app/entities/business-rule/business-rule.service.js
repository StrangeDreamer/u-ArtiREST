(function() {
    'use strict';
    angular
        .module('gatewayApp')
        .factory('BusinessRule', BusinessRule);

    BusinessRule.$inject = ['$resource'];

    function BusinessRule ($resource) {
        var resourceUrl =  'businessrule/' + 'api/business-rules/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
