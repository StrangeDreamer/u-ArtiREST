(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('BusinessRuleDialogController', BusinessRuleDialogController);

    BusinessRuleDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'BusinessRule'];

    function BusinessRuleDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, BusinessRule) {
        var vm = this;

        vm.businessRule = entity;
        $scope.businessRule1 = {
            action: {name: null, service: null,transitions: []},
            preConditions: [],
            postConditions: []
        };
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        //allowedArtifacts实际上就是这个br能够用于什么artifact上，等同于brclass的value，目前只能支持一个br对应一个artifact

        $scope.allowedRuleType = ["INSTATE","ATTRIBUTE_DEFINED","SCALAR_COMPARISON"];
        $scope.allowedOperators = ["EQUAL","LARGER","LESS"];
        $scope.newRule = {
            type:null,
            artifact: null,
            attribute: null,
            state: null,
            value: null,
            operator: null
        };
        $scope.postNewRule = {
            type:null,
            artifact: null,
            attribute: null,
            state: null,
            value: null,
            operator: null
        };
        $scope.addNewRuleToPreConditions = function(){
            $scope.addNewRule($scope.businessRule1.preConditions, $scope.newRule, $scope.editNewPreRuleIdx);
            $scope.newRule = {
                type:null,
                artifact: null,
                attribute: null,
                state: null,
                value: null,
                operator: null
            };
            $scope.editNewPreRuleIdx = -1;
            vm.businessRule.preConditions = $scope.businessRule1.preConditions;
            console.log(JSON.stringify($scope.businessRule1.preConditions));
        };
        $scope.addNewRuleToPostConditions = function(){
            $scope.addNewRule($scope.businessRule1.postConditions, $scope.postNewRule, $scope.editNewPostRuleIdx);
            $scope.postNewRule = {
                type:null,
                artifact: null,
                attribute: null,
                state: null,
                value: null,
                operator: null
            };
            $scope.editNewPostRuleIdx = -1;
            vm.businessRule.postConditions = $scope.businessRule1.postConditions;
        };

        $scope.addNewRule = function(conditions, rule, index){
            console.log("add new rule");
            console.log(JSON.stringify(conditions));
            console.log(JSON.stringify(rule));
            console.log(JSON.stringify(index));
            if(rule.type && rule.artifact){
                var newRule = {
                    type:rule.type,
                    artifact: rule.artifact,
                    attribute: rule.attribute,
                    state: rule.state,
                    value: rule.value,
                    operator: rule.operator
                };
                if(index===-1)
                    conditions.push(newRule);
            }
        };

        $scope.editNewPreRuleIdx = -1;
        $scope.editPreAtom = function(atom){
            $scope.newRule = atom;
            $scope.editNewPreRuleIdx = $scope.businessRule1.preConditions.indexOf(atom);
        };

        $scope.editNewPostRuleIdx = -1;
        $scope.editPostAtom = function(atom){
            $scope.postNewRule = atom;
            $scope.editNewPostRuleIdx = $scope.businessRule1.postConditions.indexOf(atom);
        };

        $scope.newTransition ={
            artifact: null, fromState: null, toState: null
        };
        $scope.addNewTransition = function(){
            if($scope.newTransition.artifact &&$scope.newTransition.fromState &&$scope.newTransition.toState){
                $scope.businessRule1.action.transitions.push($scope.newTransition);
                $scope.newTransition ={
                    artifact: null, fromState: null, toState: null
                };
                vm.businessRule.action.transitions = $scope.businessRule1.action.transitions;
            }
        };

        function save () {
            vm.isSaving = true;
            vm.businessRule.action.transitions = $scope.businessRule1.action.transitions;
            vm.businessRule.preConditions = $scope.businessRule1.preConditions;
            vm.businessRule.postConditions = $scope.businessRule1.postConditions;
            if (vm.businessRule.id !== null) {
                BusinessRule.update(vm.businessRule, onSaveSuccess, onSaveError);
            } else {
                BusinessRule.save(vm.businessRule, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('gatewayApp:businessRuleUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
