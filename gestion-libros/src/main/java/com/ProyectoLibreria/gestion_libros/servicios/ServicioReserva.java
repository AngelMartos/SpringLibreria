package com.ProyectoLibreria.gestion_libros.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ProyectoLibreria.gestion_libros.Repositorio.ReservaRepositorio;
import com.ProyectoLibreria.gestion_libros.modelos.Reserva;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioReserva {

    private final ReservaRepositorio reservaRepositorio;

    @Autowired
    public ServicioReserva(ReservaRepositorio reservaRepositorio) {
        this.reservaRepositorio = reservaRepositorio;
    }

    public List<Reserva> getAllReservas() {
        return reservaRepositorio.findAll();
    }

    public Optional<Reserva> getReservaById(Long id) {
        return reservaRepositorio.findById(id);
    }

    public Reserva createReserva(Reserva reserva) {
        return reservaRepositorio.save(reserva);
    }

    public Reserva updateReserva(Long id, Reserva reservaDetails) {
        Optional<Reserva> reservaOptional = reservaRepositorio.findById(id);
        if (reservaOptional.isPresent()) {
            Reserva reserva = reservaOptional.get();

            reserva.setUsuario(reservaDetails.getUsuario());
            reserva.setLibro(reservaDetails.getLibro());
            reserva.setCantidad(reservaDetails.getCantidad());
            reserva.setFechaReserva(reservaDetails.getFechaReserva());

            return reservaRepositorio.save(reserva);
        }
        return null;
    }

    public boolean deleteReserva(Long id) {
        Optional<Reserva> reservaOptional = reservaRepositorio.findById(id);
        if (reservaOptional.isPresent()) {
            reservaRepositorio.deleteById(id);
            return true;
        }
        return false;
    }
}

