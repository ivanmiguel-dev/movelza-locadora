
/*
 MOVELZA - SISTEMA DE ESTOQUE
 Frota inicial: 220 veículos
*/

(function () {

  const grupos = [

    ["economico", [
      ["Fiat Mobi", 10, "Flex"],
      ["Renault Kwid", 10, "Flex"]
    ]],

    ["hatch", [
      ["Volkswagen Polo", 5, "Flex"],
      ["Chevrolet Onix Hatch", 5, "Flex"],
      ["Hyundai HB20", 5, "Flex"],
      ["Renault Sandero", 5, "Flex"]
    ]],

    ["sedan", [
      ["Chevrolet Onix Plus", 5, "Flex"],
      ["Volkswagen Virtus", 5, "Flex"],
      ["Fiat Cronos", 5, "Flex"],
      ["Hyundai HB20S", 5, "Flex"]
    ]],

    ["suv", [
      ["Volkswagen T-Cross", 5, "Flex"],
      ["Hyundai Creta", 5, "Flex"],
      ["Toyota Corolla Cross", 5, "Flex"],
      ["Honda HR-V", 5, "Flex"]
    ]],

    ["7lugares", [
      ["Toyota SW4", 5, "Diesel"],
      ["Jeep Commander", 5, "Flex"],
      ["Chevrolet Spin", 5, "Flex"],
      ["Mitsubishi Outlander", 5, "Gasolina"]
    ]],

    ["picapes", [
      ["Fiat Strada", 5, "Flex"],
      ["Chevrolet Montana", 5, "Flex"],
      ["Fiat Toro", 5, "Diesel"],
      ["Ford Ranger", 5, "Diesel"],
      ["Toyota Hilux", 5, "Diesel"],
      ["Chevrolet S10", 5, "Diesel"]
    ]],

    ["utilitarios", [
      ["Fiat Fiorino", 10, "Flex"],
      ["Renault Kangoo", 5, "Flex"],
      ["Peugeot Partner Rapid", 5, "Flex"]
    ]],

    ["vans", [
      ["Renault Master", 5, "Diesel"],
      ["Fiat Ducato", 5, "Diesel"]
    ]],

    ["premium", [
      ["BMW 320i", 4, "Gasolina"],
      ["Mercedes-Benz C 200", 4, "Gasolina"],
      ["Audi A3 Sedan", 3, "Gasolina"],
      ["Volvo EX30", 3, "Elétrico"],
      ["BMW X1", 3, "Gasolina"],
      ["Audi Q3", 3, "Gasolina"]
    ]],

    ["eletricos", [
      ["BYD Dolphin", 5, "Elétrico"],
      ["BYD Song Plus", 5, "Híbrido"],
      ["BYD Seal", 5, "Elétrico"],
      ["BYD Yuan Plus", 5, "Elétrico"],
      ["BYD Dolphin Mini", 5, "Elétrico"],
      ["BYD Tan", 5, "Elétrico"],
      ["BYD King", 5, "Híbrido"],
      ["BYD Seal U", 5, "Híbrido"]
    ]]

  ];

  const cores = [
    "Branco",
    "Preto",
    "Cinza",
    "Prata"
  ];

  const estoque = [];

  grupos.forEach(function (grupo) {

    const categoria = grupo[0];
    const modelos = grupo[1];

    modelos.forEach(function (dados) {

      const modelo = dados[0];
      const quantidade = dados[1];
      const combustivel = dados[2];

      for (let unidade = 1;
           unidade <= quantidade;
           unidade++) {

        const numero = estoque.length + 1;

        const id = "MOV-" +
          String(numero).padStart(3, "0");

        const cor = cores[
        estoque.length % cores.length
          ];

        const veiculo = {

          id: id,
          categoria: categoria,
          modelo: modelo,
          unidade: unidade,
          cor: cor,
          combustivel: combustivel,
          status: "disponivel",
          placa: null,
          diaria: null

        };

        estoque.push(veiculo);

      }

    });

  });

  if (estoque.length !== 220) {
    throw new Error(
      "Erro: o estoque deve conter 220 veículos."
    );
  }

  window.MOVELZA_ESTOQUE = estoque;

  console.log(
    "MOVELZA: " +
    estoque.length +
    " veículos carregados."
  );

})();
