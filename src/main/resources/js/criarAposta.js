// Note: please restart the page if syntax highlighting works bad.
let array = [[10,53,5,37,23,34],[21,55,26,15,22,3],[21,26,55,10,53,5],[10,53,5,30,34,23],[10,53,5,34,37,8],[21,15,55,26,22,7],[55,15,21,10,53,44],[10,53,11,35,30,5],[10,53,35,11,30,44],[21,15,55,47,7,3],[46,53,22,47,58,4],[10,53,35,38,11,37],[55,15,21,10,35,53],[55,47,22,30,51,31],[47,21,31,7,51,55],[10,35,11,37,38,34],[28,1,58,38,9,27],[15,21,24,38,10,35],[38,10,35,34,37,41],[21,24,15,41,10,35],[21,24,18,32,38,10],[24,15,21,10,32,38],[15,19,24,14,32,10],[28,44,43,4,59,36],[1,2,3,4,5,6],[31,46,59,15,9,19],[34,49,32,8,58,33],[41,10,20,13,60,36],[21,4,20,13,38,51],[56,2,12,1,14,60],[2,43,41,33,59,46],[60,38,1,34,15,19],[20,39,45,15,2,27],[13,25,35,46,40,36],[30,34,25,51,1,23],[9,51,49,14,38,28],[30,34,16,11,6,37],[51,6,53,34,8,59],[21,54,39,19,55,9],[21,31,46,11,2,20],[45,6,26,39,5,44],[58,12,57,8,46,26],[24,11,49,20,15,45],[24,35,54,33,50,29],[18,19,35,8,12,15],[56,54,58,33,53,57],[20,56,6,3,12,9],[59,34,45,35,2,25],[28,46,12,34,31,26],[46,39,29,37,9,49],[39,2,46,54,11,34],[18,9,58,55,24,16],[50,52,59,18,12,11],[55,22,26,54,29,58],[16,57,41,5,22,26],[52,54,18,6,35,27],[54,60,38,47,2,6],[46,9,14,40,53,45],[49,3,40,47,28,2],[38,54,43,47,3,57],[25,7,10,26,4,32],[36,44,45,19,29,35],[57,5,54,47,2,33],[58,20,41,42,12,25],[3,55,57,15,51,13],[10,39,46,21,56,26],[55,17,13,41,46,52],[25,32,30,41,54,46],[50,5,60,39,12,33],[47,12,44,31,32,25],[16,36,17,1,49,28],[15,19,37,1,44,29],[44,46,4,23,60,51],[26,29,2,12,44,60],[43,31,50,5,15,59],[17,36,24,34,58,2],[11,12,1,14,16,10],[2,14,35,24,31,6],[17,27,52,59,1,40],[45,48,11,9,56,16],[21,10,56,38,34,53],[10,1,41,37,58,47],[37,8,40,50,51,14],[7,37,48,46,45,52],[30,19,25,33,18,13],[40,15,37,48,7,43],[51,26,40,47,53,16],[25,2,9,49,32,11],[19,29,34,14,13,49],[1,18,8,30,43,4],[44,55,36,3,31,59],[7,17,18,25,14,21],[12,14,41,26,47,7],[55,43,57,16,3,19],[37,8,32,30,57,31],[20,33,13,46,3,48],[14,16,22,20,51,3],[40,44,47,42,48,11],[60,15,37,34,38,14],[4,56,46,6,2,10]];

array.forEach(aposta => {
    // setInterval(function () {
        aposta.forEach(numero => {
            let idNum = "";
            if(numero.toString().length == 1){
                idNum = "n0" + numero;
            }else{
                idNum = "n" + numero;
            }

            document.getElementById(idNum).click();
        });
        document.getElementById("colocarnocarrinho").click();
        console.log("Aposta Feita: " + aposta);
    // }, 10000);
})