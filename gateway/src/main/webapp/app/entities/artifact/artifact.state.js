(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('artifact', {
            parent: 'entity',
            url: '/artifact?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Artifacts'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/artifact/artifacts.html',
                    controller: 'ArtifactController',
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
        .state('artifact-detail', {
            parent: 'artifact',
            url: '/artifact/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Artifact'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/artifact/artifact-detail.html',
                    controller: 'ArtifactDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Artifact', function($stateParams, Artifact) {
                    return Artifact.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'artifact',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('artifact-detail.edit', {
            parent: 'artifact-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/artifact/artifact-dialog.html',
                    controller: 'ArtifactDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Artifact', function(Artifact) {
                            return Artifact.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('artifact.new', {
            parent: 'artifact',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/artifact/artifact-dialog.html',
                    controller: 'ArtifactDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                currentState: null,
                                createdAt: null,
                                updatedAt: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('artifact', null, { reload: 'artifact' });
                }, function() {
                    $state.go('artifact');
                });
            }]
        })
        .state('artifact.edit', {
            parent: 'artifact',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/artifact/artifact-dialog.html',
                    controller: 'ArtifactDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Artifact', function(Artifact) {
                            return Artifact.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('artifact', null, { reload: 'artifact' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('artifact.delete', {
            parent: 'artifact',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/artifact/artifact-delete-dialog.html',
                    controller: 'ArtifactDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Artifact', function(Artifact) {
                            return Artifact.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('artifact', null, { reload: 'artifact' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
