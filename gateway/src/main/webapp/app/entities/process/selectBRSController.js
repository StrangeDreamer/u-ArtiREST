(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('selectBRSController', SelectBRSController);

    SelectBRSController.$inject = ['$scope', '$rootScope', '$state', '$uibModal', '$http', '$log',
        'entity', 'processModel', 'ArtifactModel', 'Process', '$uibModalInstance'];

    function SelectBRSController($scope, $rootScope, $state, $uibModal, $http, $log,
                                 entity, processModel, ArtifactModel, Process,$uibModalInstance) {
        $scope.BRS = entity;
        $scope.processModel = processModel;

        $scope.businessRuleClass = [];
        $scope.serviceClass = [];

        $scope.businessRuleClasses = $scope.processModel.businessRuleClass;
        $scope.serviceClasses = $scope.processModel.serviceClass;

        //显示到列表里的待选数据
        $scope.businessRules = [];
        $scope.services = [];

        $scope.selectedBR = {
            class: {}
        };

        $scope.selectedService = {
            class: {}
        };

        //根据processModel里面存的service和br的class数组获取具体的s和br
        $scope.loadAll = function(){
            $scope.businessRules = [];
            $scope.services =[];
            $scope.selectedBR.class={};
            $scope.selectedService.class={};


            console.log("$scope.businessRuleClasses:",$scope.businessRuleClasses);
            for(var i=0; i<$scope.businessRuleClasses.length; i++){
                $scope.selectedBR.class[$scope.businessRuleClasses[i]] = {id:null};
                $http.get('businessrule/api/business-rules/class/' + $scope.businessRuleClasses[i]).then(function (res) {
                    console.log("res",res);
                    for(var x = 0; x<res.data.length; x++){
                        $scope.businessRules.push(res.data[x]);
                    }

                })
            }

            console.log("$scope.serviceClasses:",$scope.serviceClasses);
            for(var j=0; j<$scope.serviceClasses.length; j++){
                $scope.selectedService.class[$scope.serviceClasses[i]] = {id:null};
                $http.get('service/api/service-models/class/' + $scope.serviceClasses[j]).then(function (res) {
                    console.log("res",res);
                    for(var y = 0; y<res.data.length; y++){
                        $scope.services.push(res.data[y]);
                    }
                })
            }

            console.log($scope.businessRules);
            console.log($scope.services);
        };

        $scope.loadAll();

        $scope.onConfirm = function () {
            $scope.BRS.businessRuleIds = [];
            $scope.BRS.serviceIds = [];

            for(var i=0; i<$scope.businessRuleClasses.length; i++){
                console.log($scope.selectedBR.class[$scope.businessRuleClasses[i]].id);
                $scope.BRS.businessRuleIds.push($scope.selectedBR.class[$scope.businessRuleClasses[i]].id);
            }

            for(var j=0; j<$scope.serviceClasses.length; j++){
                console.log($scope.selectedService.class[$scope.serviceClasses[j]].id);
                $scope.BRS.serviceIds.push($scope.selectedService.class[$scope.serviceClasses[j]].id);
            }


            $uibModalInstance.close($scope.BRS);
        };

        var unsubscribe = $rootScope.$on('gatewayApp:processModelUpdate', function(event, result) {
            $scope.processModel = result;
        });

        $scope.$on('$destroy', unsubscribe);
        $scope.key = 0;


    }
})();
