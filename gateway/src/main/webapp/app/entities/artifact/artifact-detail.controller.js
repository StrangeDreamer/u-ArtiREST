(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ArtifactDetailController', ArtifactDetailController);

    ArtifactDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Artifact'];

    function ArtifactDetailController($scope, $rootScope, $stateParams, previousState, entity, Artifact) {
        var vm = this;

        vm.artifact = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('gatewayApp:artifactUpdate', function(event, result) {
            vm.artifact = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
