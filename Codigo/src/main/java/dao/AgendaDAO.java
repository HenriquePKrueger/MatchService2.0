package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Agenda;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AgendaDAO extends DAO {

    public AgendaDAO() {
        super();
    }

    public void removerHorario(long agendaId, long prestadorId) throws SQLException {
        String sql = "DELETE FROM agenda " +
                     "WHERE id = ? " +
                     "AND prestadores_id = ? " +
                     "AND status_horario = 0";

        try (PreparedStatement ps = db.prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            ps.setLong(2, prestadorId);
            ps.executeUpdate();
        }
    }
    
    public void inserirHorario(Agenda a) throws SQLException {
        String sql = "INSERT INTO agenda " +
                     "(prestadores_id, data_disponivel, horario_inicio, horario_fim, status_horario) " +
                     "VALUES (?, ?, ?, ?, 0)";

        try (PreparedStatement ps = db.prepareStatement(sql)) {
            ps.setInt(1, a.getPrestadoresId());
            ps.setDate(2, Date.valueOf(a.getDataDisponivel()));
            ps.setTime(3, Time.valueOf(a.getHorarioInicio() + ":00"));
            ps.setTime(4, Time.valueOf(a.getHorarioFim() + ":00"));
            ps.executeUpdate();
        }
    }
    public List<Agenda> listarPorPrestador(long prestadorId) {
        String sql = "SELECT * " +
                     "FROM agenda " +
                     "WHERE prestadores_id = ? " +
                     "ORDER BY data_disponivel, horario_inicio";

        List<Agenda> lista = new ArrayList<>();

        try (PreparedStatement ps = db.prepareStatement(sql)) {
            ps.setLong(1, prestadorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAgenda(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar agenda: " + e.getMessage());
        }

        return lista;
    }

    public List<Agenda> listarLivresPorPrestador(long prestadorId) {
        String sql = "SELECT * " +
                     "FROM agenda " +
                     "WHERE prestadores_id = ? " +
                     "AND status_horario = 0 " +
                     "AND data_disponivel >= CURRENT_DATE " +
                     "ORDER BY data_disponivel, horario_inicio";

        List<Agenda> lista = new ArrayList<>();

        try (PreparedStatement ps = db.prepareStatement(sql)) {
            ps.setLong(1, prestadorId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAgenda(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar horários livres: " + e.getMessage());
        }

        return lista;
    }

    public boolean reservarHorario(long agendaId, long clienteId) throws SQLException {
        String buscar = "SELECT prestadores_id " +
                        "FROM agenda " +
                        "WHERE id = ? " +
                        "AND status_horario = 0 " +
                        "FOR UPDATE";

        String atualizar = "UPDATE agenda " +
                           "SET status_horario = 1 " +
                           "WHERE id = ? " +
                           "AND status_horario = 0";

        String inserirAgendamento = "INSERT INTO agendamentos " +
                                    "(clientes_id, prestadores_id, agenda_id, status) " +
                                    "VALUES (?, ?, ?, 1)";

        try {
            db.setAutoCommit(false);

            long prestadorId = -1;

            try (PreparedStatement ps = db.prepareStatement(buscar)) {
                ps.setLong(1, agendaId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        prestadorId = rs.getLong("prestadores_id");
                    } else {
                        db.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement ps = db.prepareStatement(atualizar)) {
                ps.setLong(1, agendaId);

                int linhas = ps.executeUpdate();

                if (linhas == 0) {
                    db.rollback();
                    return false;
                }
            }

            try (PreparedStatement ps = db.prepareStatement(inserirAgendamento)) {
                ps.setLong(1, clienteId);
                ps.setLong(2, prestadorId);
                ps.setLong(3, agendaId);
                ps.executeUpdate();
            }

            db.commit();
            return true;

        } catch (SQLException e) {
            db.rollback();
            throw e;
        } finally {
            db.setAutoCommit(true);
        }
    }
    
    public boolean concluirHorarioPorSolicitacaoDireta(long solicitacaoId, long prestadorId) throws SQLException {
        String sql = "UPDATE agenda " +
                     "SET status_horario = 2 " +
                     "WHERE id = (" +
                     "    SELECT agenda_id " +
                     "    FROM solicitacoes_servicos " +
                     "    WHERE id = ? " +
                     "    AND prestadores_id = ? " +
                     "    AND tipo_solicitacao = 0" +
                     ")";

        try (PreparedStatement ps = db.prepareStatement(sql)) {
            ps.setLong(1, solicitacaoId);
            ps.setLong(2, prestadorId);

            return ps.executeUpdate() > 0;
        }
    }
    
    private Agenda mapearAgenda(ResultSet rs) throws SQLException {
        Agenda a = new Agenda();

        a.setId(rs.getInt("id"));
        a.setPrestadoresId(rs.getInt("prestadores_id"));
        
        LocalDate data = rs.getDate("data_disponivel").toLocalDate();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        a.setDataDisponivel(data.format(formato));
        
        a.setHorarioInicio(rs.getTime("horario_inicio").toString().substring(0, 5));
        a.setHorarioFim(rs.getTime("horario_fim").toString().substring(0, 5));
        a.setStatusHorario(rs.getInt("status_horario"));

        return a;
    }
}