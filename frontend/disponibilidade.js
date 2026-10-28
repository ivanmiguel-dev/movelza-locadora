
/*
 MOVELZA - CONTROLE DE DISPONIBILIDADE
 Verificação de conflitos entre períodos de locação.

 Ambiente de demonstração:
 não confirma nem bloqueia reservas reais.
*/

(function () {
  "use strict";

  function converterData(valor) {
    if (!valor) return null;

    const data = new Date(valor);

    if (Number.isNaN(data.getTime())) {
      return null;
    }

    return data;
  }

  function periodosConflitam(
    inicioA,
    fimA,
    inicioB,
    fimB
  ) {
    const a1 = converterData(inicioA);
    const a2 = converterData(fimA);
    const b1 = converterData(inicioB);
    const b2 = converterData(fimB);

    if (!a1 || !a2 || !b1 || !b2) {
      return false;
    }

    if (a1 >= a2 || b1 >= b2) {
      return false;
    }

    return a1 < b2 && b1 < a2;
  }

  function statusAprovado(status) {
    const texto = String(status || "")
      .toLowerCase()
      .trim();

    return [
      "aprovada",
      "aprovado",
      "aprovada (teste)",
      "aprovado (teste)"
    ].includes(texto);
  }

  function verificarDisponibilidade(
    veiculo,
    retirada,
    devolucao,
    reservas
  ) {
    const inicio = converterData(retirada);
    const fim = converterData(devolucao);

    if (!inicio || !fim || inicio >= fim) {
      return {
        disponivel: false,
        motivo: "Período inválido."
      };
    }

    if (!veiculo || !veiculo.id) {
      return {
        disponivel: false,
        motivo: "Veículo não encontrado."
      };
    }

    if (veiculo.status === "manutencao") {
      return {
        disponivel: false,
        motivo: "Veículo em manutenção."
      };
    }

    if (veiculo.status === "reservado") {
      return {
        disponivel: false,
        motivo: "Veículo bloqueado no estoque."
      };
    }

    const lista = Array.isArray(reservas)
      ? reservas
      : [];

    const conflito = lista.find(function (reserva) {

      const unidade =
        reserva.unidade ||
        reserva.veiculoId ||
        reserva.idVeiculo;

      const status =
        reserva.status ||
        reserva.situacao;

      if (unidade !== veiculo.id) {
        return false;
      }

      if (!statusAprovado(status)) {
        return false;
      }

      return periodosConflitam(
        retirada,
        devolucao,
        reserva.retirada,
        reserva.devolucao
      );
    });

    if (conflito) {
      return {
        disponivel: false,
        motivo: "Já existe uma reserva aprovada nesse período.",
        conflito: conflito
      };
    }

    return {
      disponivel: true,
      motivo: "Veículo disponível para o período informado."
    };
  }

  window.MOVELZA_DISPONIBILIDADE = {
    converterData: converterData,
    periodosConflitam: periodosConflitam,
    verificarDisponibilidade: verificarDisponibilidade
  };

  console.log(
    "MOVELZA: módulo de disponibilidade carregado."
  );

})();
