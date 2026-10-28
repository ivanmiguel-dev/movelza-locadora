
(function () {
  "use strict";

  const CHAVE_ESTOQUE = "MOVELZA_ESTOQUE_EDICOES_V1";
  const PREFIXO_DEMO = "ZZZ";

  function gerarPlaca(numero) {
    // Padrão visual Mercosul: LLLNLNN.
    // Prefixo reservado aqui apenas como convenção
    // interna do projeto, sem garantia de inexistência
    // de placas reais semelhantes.
    const letra = String.fromCharCode(
      65 + Math.floor(numero / 1000) % 26
    );

    const digito1 = Math.floor(numero / 100) % 10;
    const digito2 = Math.floor(numero / 10) % 10;
    const digito3 = numero % 10;

    return (
      PREFIXO_DEMO +
      digito1 +
      letra +
      digito2 +
      digito3
    );
  }

  function executarEmplacamento() {
    const estoque = window.MOVELZA_ESTOQUE;

    if (!Array.isArray(estoque) || estoque.length === 0) {
      alert("O estoque não foi carregado.");
      return;
    }

    coes = {};

    try {let edi
      edicoes = JSON.parse(
        localStorage.getItem(CHAVE_ESTOQUE) || "{}"
      );

      if (
        !edicoes ||
        typeof edicoes !== "object" ||
        Array.isArray(edicoes)
      ) {
        edicoes = {};
      }
    } catch (erro) {
      alert("Não foi possível ler os dados do estoque.");
      return;
    }

    const placasExistentes = new Set();

    for (const veiculo of estoque) {
      const placa = (
        edicoes[veiculo.id]?.placa ||
        veiculo.placa ||
        ""
      ).toUpperCase().replace(/[\s-]/g, "");

      if (placa) {
        placasExistentes.add(placa);
      }
    }

    let geradas = 0;
    let preservadas = 0;
    let contador = 1;

    const novasEdicoes = { ...edicoes };

    for (const veiculo of estoque) {
      const dados = novasEdicoes[veiculo.id] || {};

      const placaAtual = (
        dados.placa ||
        veiculo.placa ||
        ""
      ).trim();

      if (placaAtual) {
        preservadas++;
        continue;
      }

      let placa;

      do {
        placa = gerarPlaca(contador);
        contador++;
      } while (placasExistentes.has(placa));

      placasExistentes.add(placa);

      novasEdicoes[veiculo.id] = {
        ...dados,
        placa: placa
      };

      geradas++;
    }

    try {
      localStorage.setItem(
        CHAVE_ESTOQUE,
        JSON.stringify(novasEdicoes)
      );
    } catch (erro) {
      alert("Erro ao salvar as placas no navegador.");
      return;
    }

    alert(
      "Emplacamento de demonstração concluído!\n\n" +
      "Placas geradas: " + geradas + "\n" +
      "Placas preservadas: " + preservadas + "\n\n" +
      "Nenhuma diária ou outra informação foi alterada."
    );

    location.reload();
  }

  window.MOVELZA_EMPLACAMENTO = {
    executar: executarEmplacamento
  };
})();
