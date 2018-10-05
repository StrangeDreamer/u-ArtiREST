(function() {
    'use strict';
    angular
        .module('gatewayApp')
        .factory('ServiceModel', ServiceModel);

    ServiceModel.$inject = ['$resource'];

    function ServiceModel ($resource) {
        var resourceUrl =  'service/' + 'api/service-models/:id';

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
