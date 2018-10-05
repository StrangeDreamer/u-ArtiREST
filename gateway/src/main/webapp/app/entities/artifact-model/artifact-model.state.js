(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('artifact-model', {
            parent: 'entity',
            url: '/artifact-model?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ArtifactModels'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/artifact-model/artifact-models.html',
                    controller: 'ArtifactModelController',
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
        .state('artifact-model-detail', {
            parent: 'artifact-model',
            url: '/artifact-model/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'ArtifactModel'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/artifact-model/artifact-model-detail.html',
                    controller: 'ArtifactModelDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'ArtifactModel', function($stateParams, ArtifactModel) {
                    return ArtifactModel.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'artifact-model',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('artifact-model-detail.edit', {
            parent: 'artifact-model-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/artifact-model/artifact-model-dialog.html',
                    controller: 'ArtifactModelDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ArtifactModel', function(ArtifactModel) {
                            return ArtifactModel.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('artifact-model.new', {
            parent: 'artifact-model',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/artifact-model/artifact-model-dialog.html',
                    controller: 'ArtifactModelDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                comment: null,
                                createdAt: null,
                                updatedAt: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('artifact-model', null, { reload: 'artifact-model' });
                }, function() {
                    $state.go('artifact-model');
                });
            }]
        })
        .state('artifact-model.edit', {
            parent: 'artifact-model',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/artifact-model/artifact-model-dialog.html',
                    controller: 'ArtifactModelDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ArtifactModel', function(ArtifactModel) {
                            return ArtifactModel.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('artifact-model', null, { reload: 'artifact-model' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('artifact-model.delete', {
            parent: 'artifact-model',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/artifact-model/artifact-model-delete-dialog.html',
                    controller: 'ArtifactModelDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ArtifactModel', function(ArtifactModel) {
                            return ArtifactModel.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('artifact-model', null, { reload: 'artifact-model' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
