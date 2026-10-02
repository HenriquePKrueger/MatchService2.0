package service;

import dao.AgendaDAO;
import dao.PrestadorPerfilDAO;
import model.PrestadorPerfil;
import spark.Request;

public class PrestadorPerfilService {
    private PrestadorPerfilDAO prestadorPerfilDAO;
    private AgendaDAO agendaDAO;

    public PrestadorPerfilService() {
        this.prestadorPerfilDAO = new PrestadorPerfilDAO();
        this.agendaDAO = new AgendaDAO();
    }

    public PrestadorPerfil carregarPerfil(Request req) {
        long prestadorId = Long.parseLong(req.params("id"));

        PrestadorPerfil perfil = prestadorPerfilDAO.buscarPerfilPorId(prestadorId);

        if (perfil != null) {
            perfil.setHorariosLivres(agendaDAO.listarLivresPorPrestador(prestadorId));
        }

        return perfil;
    }
}