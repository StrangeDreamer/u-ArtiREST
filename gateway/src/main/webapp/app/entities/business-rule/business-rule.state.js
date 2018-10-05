(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('business-rule', {
            parent: 'entity',
            url: '/business-rule?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'BusinessRules'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/business-rule/business-rules.html',
                    controller: 'BusinessRuleController',
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
        .state('business-rule-detail', {
            parent: 'business-rule',
            url: '/business-rule/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'BusinessRule'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/business-rule/business-rule-detail.html',
                    controller: 'BusinessRuleDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'BusinessRule', function($stateParams, BusinessRule) {
                    return BusinessRule.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'business-rule',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('business-rule-detail.edit', {
            parent: 'business-rule-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/business-rule/business-rule-dialog.html',
                    controller: 'BusinessRuleDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['BusinessRule', function(BusinessRule) {
                            return BusinessRule.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('business-rule.new', {
            parent: 'business-rule',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/business-rule/business-rule-dialog.html',
                    controller: 'BusinessRuleDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                businessRuleClass: null,
                                id: null,
                                action: {name: null, service: null,transitions: []},
                                preConditions: [],
                                postConditions: []
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('business-rule', null, { reload: 'business-rule' });
                }, function() {
                    $state.go('business-rule');
                });
            }]
        })
        .state('business-rule.edit', {
            parent: 'business-rule',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/business-rule/business-rule-dialog.html',
                    controller: 'BusinessRuleDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['BusinessRule', function(BusinessRule) {
                            return BusinessRule.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('business-rule', null, { reload: 'business-rule' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('business-rule.delete', {
            parent: 'business-rule',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/business-rule/business-rule-delete-dialog.html',
                    controller: 'BusinessRuleDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['BusinessRule', function(BusinessRule) {
                            return BusinessRule.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('business-rule', null, { reload: 'business-rule' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
