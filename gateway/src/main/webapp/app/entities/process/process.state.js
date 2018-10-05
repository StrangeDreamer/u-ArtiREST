(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('process', {
            parent: 'entity',
            url: '/process?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Processes'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/process/processes.html',
                    controller: 'ProcessController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
            }
        })
        .state('process-detail', {
            parent: 'process',
            url: '/process/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Process'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/process/process-detail.html',
                    controller: 'ProcessDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Process', function($stateParams, Process) {
                    return Process.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'process',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('process-detail.edit', {
            parent: 'process-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/process/process-dialog.html',
                    controller: 'ProcessDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Process', function(Process) {
                            return Process.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('process.new', {
            parent: 'process',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/process/process-dialog.html',
                    controller: 'ProcessDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                isRunning: null,
                                createdAt: null,
                                updatedAt: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('process', null, { reload: 'process' });
                }, function() {
                    $state.go('process');
                });
            }]
        })
        .state('process.edit', {
            parent: 'process',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/process/process-dialog.html',
                    controller: 'ProcessDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Process', function(Process) {
                            return Process.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('process', null, { reload: 'process' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('process.delete', {
            parent: 'process',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/process/process-delete-dialog.html',
                    controller: 'ProcessDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Process', function(Process) {
                            return Process.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('process', null, { reload: 'process' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
