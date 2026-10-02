package service;

import java.sql.SQLException;
import java.util.List;

import dao.AgendaDAO;
import dao.PrestadorDAO;
import dao.PrestadorDAO.NivelDetalhePrestador;
import model.Agenda;
import model.Prestador;
import spark.Request;
import spark.Response;
import spark.Spark;

public class AgendaService {
    private AgendaDAO agendaDAO;
    private PrestadorDAO prestadorDAO;

    public AgendaService() {
        this.agendaDAO = new AgendaDAO();
        this.prestadorDAO = new PrestadorDAO();
    }

    public List<Agenda> carregarAgendaDoPrestador(Request req) {
        Long userId = req.session().attribute("user_id");

        Prestador p = prestadorDAO.selectPrestador("usuarios_id", userId, NivelDetalhePrestador.SIMPLES);

        if (p == null) {
            return null;
        }

        return agendaDAO.listarPorPrestador(p.getId());
    }

    public void criarHorario(Request req, Response res) {
        Long userId = req.session().attribute("user_id");

        Prestador p = prestadorDAO.selectPrestador("usuarios_id", userId, NivelDetalhePrestador.SIMPLES);

        if (p == null) {
            res.redirect("/");
            Spark.halt();
        }

        String data = getParamSafe(req, "data");
        String inicio = getParamSafe(req, "inicio");
        String fim = getParamSafe(req, "fim");

        if (isBlank(data) || isBlank(inicio) || isBlank(fim)) {
            req.session().attribute("erro", "Preencha todos os campos da agenda.");
            res.redirect("/agenda");
            Spark.halt();
        }

        if (inicio.compareTo(fim) >= 0) {
            req.session().attribute("erro", "O horário inicial deve ser menor que o horário final.");
            res.redirect("/agenda");
            Spark.halt();
        }

        Agenda a = new Agenda();
        a.setPrestadoresId((int) p.getId());
        a.setDataDisponivel(data);
        a.setHorarioInicio(inicio);
        a.setHorarioFim(fim);

        try {
            agendaDAO.inserirHorario(a);
            req.session().attribute("sucesso", "Horário cadastrado com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao criar horário: " + e.getMessage());
            req.session().attribute("erro", "Erro ao cadastrar horário.");
        }

        res.redirect("/agenda");
    }

    public void removerHorario(Request req, Response res) {
        Long userId = req.session().attribute("user_id");

        Prestador p = prestadorDAO.selectPrestador("usuarios_id", userId, NivelDetalhePrestador.SIMPLES);

        if (p == null) {
            res.redirect("/");
            Spark.halt();
        }

        long agendaId = Long.parseLong(req.params("id"));

        try {
            agendaDAO.removerHorario(agendaId, p.getId());
            req.session().attribute("sucesso", "Horário removido com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao remover horário: " + e.getMessage());
            req.session().attribute("erro", "Remova a solicitação cancelada da lista de solicitações diretas antes de excluir este horário.");
        }

        res.redirect("/agenda");
    }

    public boolean reservarHorario(Request req, Response res) {
        Long clienteId = req.session().attribute("user_id");

        if (clienteId == null) {
            res.status(401);
            return false;
        }

        String agendaIdStr = req.queryParams("agenda_id");

        if (isBlank(agendaIdStr)) {
            res.status(400);
            return false;
        }

        try {
            long agendaId = Long.parseLong(agendaIdStr);
            boolean ok = agendaDAO.reservarHorario(agendaId, clienteId);

            res.status(ok ? 200 : 409);
            return ok;

        } catch (Exception e) {
            System.err.println("Erro ao reservar horário: " + e.getMessage());
            res.status(500);
            return false;
        }
    }

    private String getParamSafe(Request req, String param) {
        String value = req.queryParams(param);
        return value == null ? "" : value.strip();
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}