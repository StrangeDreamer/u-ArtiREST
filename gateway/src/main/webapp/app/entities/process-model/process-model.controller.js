(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ProcessModelController', ProcessModelController);

    ProcessModelController.$inject = ['$scope', '$state', 'ProcessModel', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function ProcessModelController($scope, $state, ProcessModel, ParseLinks, AlertService, paginationConstants, pagingParams) {

        $scope.loadPage = loadPage;
        $scope.predicate = pagingParams.predicate;
        $scope.reverse = pagingParams.ascending;
        $scope.transition = transition;
        $scope.itemsPerPage = paginationConstants.itemsPerPage;

        loadAll();

        function loadAll () {
            ProcessModel.query({
                page: pagingParams.page - 1,
                size: $scope.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [$scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc')];
                if ($scope.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                $scope.totalItems = headers('X-Total-Count');
                $scope.queryCount = $scope.totalItems;
                $scope.processModels = data;
                $scope.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function loadPage(page) {
            $scope.page = page;
            $scope.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: $scope.page,
                sort: $scope.predicate + ',' + ($scope.reverse ? 'asc' : 'desc'),
                search: $scope.currentSearch
            });
        }

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.processModel = {
                name: null,
                comment: null,
                status: null,
                createdAt: null,
                updatedAt: null,
                id: null
            };
        };
    }
})();
