(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('service-model', {
            parent: 'entity',
            url: '/service-model?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ServiceModels'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/service-model/service-models.html',
                    controller: 'ServiceModelController',
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
        .state('service-model-detail', {
            parent: 'service-model',
            url: '/service-model/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ServiceModel'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/service-model/service-model-detail.html',
                    controller: 'ServiceModelDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'ServiceModel', function($stateParams, ServiceModel) {
                    return ServiceModel.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'service-model',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('service-model-detail.edit', {
            parent: 'service-model-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/service-model/service-model-dialog.html',
                    controller: 'ServiceModelDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ServiceModel', function(ServiceModel) {
                            return ServiceModel.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('service-model.new', {
            parent: 'service-model',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/service-model/service-model-dialog.html',
                    controller: 'ServiceModelDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                serviceClass: null,
                                url: null,
                                method: null,
                                inputArtifact: null,
                                outputArtifact: null,
                                comment: null,
                                type: 'HUMAN_TASK',
                                id: null,
                                inputParams: null,
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('service-model', null, { reload: 'service-model' });
                }, function() {
                    $state.go('service-model');
                });
            }]
        })
        .state('service-model.edit', {
            parent: 'service-model',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/service-model/service-model-dialog.html',
                    controller: 'ServiceModelDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ServiceModel', function(ServiceModel) {
                            return ServiceModel.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('service-model', null, { reload: 'service-model' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('service-model.delete', {
            parent: 'service-model',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/service-model/service-model-delete-dialog.html',
                    controller: 'ServiceModelDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ServiceModel', function(ServiceModel) {
                            return ServiceModel.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('service-model', null, { reload: 'service-model' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
