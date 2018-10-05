(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ArtifactModelDetailController', ArtifactModelDetailController);

    ArtifactModelDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ArtifactModel'];

    function ArtifactModelDetailController($scope, $rootScope, $stateParams, previousState, entity, ArtifactModel) {
        var vm = this;

        vm.artifactModel = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('gatewayApp:artifactModelUpdate', function(event, result) {
            vm.artifactModel = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
