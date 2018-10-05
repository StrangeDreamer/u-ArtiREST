(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ProcessModelDeleteController',ProcessModelDeleteController);

    ProcessModelDeleteController.$inject = ['$scope','$uibModalInstance', 'entity', 'ProcessModel'];

    function ProcessModelDeleteController($scope, $uibModalInstance, entity, ProcessModel) {
        $scope.processModel = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            ProcessModel.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
        // var vm = this;
        //
        // vm.processModel = entity;
        // vm.clear = clear;
        // vm.confirmDelete = confirmDelete;
        //
        // function clear () {
        //     $uibModalInstance.dismiss('cancel');
        // }
        //
        // function confirmDelete (id) {
        //     ProcessModel.delete({id: id},
        //         function () {
        //             $uibModalInstance.close(true);
        //         });
        // }
    }
})();
