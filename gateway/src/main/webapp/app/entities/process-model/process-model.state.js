(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('process-model', {
            parent: 'entity',
            url: '/process-model?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ProcessModels'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/process-model/process-models.html',
                    controller: 'ProcessModelController'
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
        .state('process-model-detail', {
            parent: 'process-model',
            url: '/process-model/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ProcessModel'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/process-model/process-model-detail.html',
                    controller: 'ProcessModelDetailController',
                    //controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'ProcessModel', function($stateParams, ProcessModel) {
                    return ProcessModel.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'process-model',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('process-model-detail.edit', {
            parent: 'process-model-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/process-model/process-model-dialog.html',
                    controller: 'ProcessModelDialogController',
                    //controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ProcessModel', function(ProcessModel) {
                            return ProcessModel.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('process-model.new', {
            parent: 'process-model',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/process-model/process-model-dialog.html',
                    controller: 'ProcessModelDialogController',
                    //controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                comment: null,
                                status: 'DESIGNING',
                                createdAt: null,
                                updatedAt: null,
                                services: [],
                                businessRules: [],
                                artifacts: [],
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('process-model', null, { reload: 'process-model' });
                }, function() {
                    $state.go('process-model');
                });
            }]
        })
        .state('process-model.edit', {
            parent: 'process-model',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/process-model/process-model-dialog.html',
                    controller: 'ProcessModelDialogController',
                    //controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ProcessModel', function(ProcessModel) {
                            return ProcessModel.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('process-model', null, { reload: 'process-model' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('process-model.delete', {
            parent: 'process-model',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/process-model/process-model-delete-dialog.html',
                    controller: 'ProcessModelDeleteController',
                    //controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ProcessModel', function(ProcessModel) {
                            return ProcessModel.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('process-model', null, { reload: 'process-model' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
