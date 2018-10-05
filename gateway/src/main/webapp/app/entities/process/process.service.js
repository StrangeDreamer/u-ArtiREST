(function() {
    'use strict';
    angular
        .module('gatewayApp')
        .factory('Process', Process);

    Process.$inject = ['$resource', 'DateUtils'];

    function Process ($resource, DateUtils) {
        var resourceUrl =  'processes/' + 'api/processes/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.createdAt = DateUtils.convertDateTimeFromServer(data.createdAt);
                        data.updatedAt = DateUtils.convertDateTimeFromServer(data.updatedAt);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
