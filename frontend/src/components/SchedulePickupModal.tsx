import React, { useState } from 'react';
import {
  X,
  Calendar,
  Clock,
  MapPin,
  CheckCircle2,
  RefreshCw
} from 'lucide-react';
import { api } from '../services/api';
import { Claim } from '../types';

interface SchedulePickupModalProps {
  claim: Claim | null;
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

const TIME_SLOTS = [
  "10:00 - 10:30 AM",
  "10:30 - 11:00 AM",
  "11:00 - 11:30 AM",
  "02:00 - 02:30 PM",
  "02:30 - 03:00 PM",
  "03:00 - 03:30 PM",
  "04:00 - 04:30 PM"
];

export const SchedulePickupModal: React.FC<SchedulePickupModalProps> = ({
  claim,
  isOpen,
  onClose,
  onSuccess
}) => {
  const [scheduledDate, setScheduledDate] = useState(
    new Date(Date.now() + 86400000).toISOString().slice(0, 10)
  );
  const [timeSlot, setTimeSlot] = useState(TIME_SLOTS[0]);
  const [pickupLocation, setPickupLocation] = useState(
    "Campus Lost & Found Central Office (Student Center Room 102)"
  );
  const [isSubmitting, setIsSubmitting] = useState(false);

  if (!isOpen || !claim) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      await api.schedulePickup({
        claimId: claim.id,
        scheduledDate: new Date(`${scheduledDate}T10:00:00`).toISOString(),
        timeSlot,
        pickupLocation
      });
      onSuccess();
      onClose();
    } catch (err) {
      console.error(err);
      alert('Failed to schedule appointment');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-white rounded-3xl max-w-lg w-full p-6 sm:p-8 shadow-2xl border border-slate-200 relative">
        <button
          onClick={onClose}
          className="absolute top-5 right-5 p-2 text-slate-400 hover:text-slate-600 rounded-full hover:bg-slate-100"
        >
          <X className="w-5 h-5" />
        </button>

        <div className="flex items-center space-x-3 mb-4">
          <div className="w-10 h-10 rounded-2xl bg-purple-100 text-purple-700 flex items-center justify-center">
            <Calendar className="w-5 h-5" />
          </div>
          <div>
            <span className="text-[10px] font-black text-purple-700 uppercase tracking-widest">
              Appointment Scheduling
            </span>
            <h3 className="text-xl font-bold text-slate-900">Schedule Collection Slot</h3>
          </div>
        </div>

        <p className="text-xs text-slate-600 mb-6">
          Claim for <strong>{claim.foundTitle}</strong> was approved. Select your preferred date and time to collect your item from campus staff.
        </p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">
              Select Pickup Date
            </label>
            <input
              type="date"
              required
              value={scheduledDate}
              onChange={(e) => setScheduledDate(e.target.value)}
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-xs font-semibold"
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">
              Available 30-Minute Slot
            </label>
            <select
              value={timeSlot}
              onChange={(e) => setTimeSlot(e.target.value)}
              className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-xs font-semibold"
            >
              {TIME_SLOTS.map((slot, i) => (
                <option key={i} value={slot}>
                  {slot}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">
              Collection Desk Location
            </label>
            <input
              type="text"
              readOnly
              value={pickupLocation}
              className="w-full px-3.5 py-2.5 bg-slate-100 border border-slate-200 rounded-xl text-xs text-slate-700 font-medium"
            />
          </div>

          <div className="flex justify-end space-x-3 pt-4 border-t border-slate-100">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 text-xs font-bold border border-slate-300 rounded-xl hover:bg-slate-50"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              className="px-6 py-2 bg-purple-600 hover:bg-purple-700 text-white rounded-xl text-xs font-bold shadow flex items-center space-x-1.5"
            >
              {isSubmitting ? (
                <>
                  <RefreshCw className="w-3.5 h-3.5 animate-spin" />
                  <span>Generating Pass...</span>
                </>
              ) : (
                <>
                  <CheckCircle2 className="w-4 h-4" />
                  <span>Confirm & Generate QR</span>
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
