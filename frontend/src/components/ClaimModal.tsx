import React, { useState } from 'react';
import {
  X,
  ShieldCheck,
  Sparkles,
  HelpCircle,
  AlertTriangle,
  ArrowRight,
  RefreshCw,
  CheckCircle2
} from 'lucide-react';
import { api } from '../services/api';
import { FoundReport } from '../types';

interface ClaimModalProps {
  item: FoundReport | null;
  lostReportId?: number;
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export const ClaimModal: React.FC<ClaimModalProps> = ({
  item,
  lostReportId,
  isOpen,
  onClose,
  onSuccess
}) => {
  const [claimantAnswers, setClaimantAnswers] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [submittedSuccess, setSubmittedSuccess] = useState(false);

  if (!isOpen || !item) return null;

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!claimantAnswers.trim()) {
      setErrorMsg('Please describe your identifying details to verify ownership.');
      return;
    }

    setIsSubmitting(true);
    setErrorMsg(null);

    try {
      await api.submitClaim({
        foundReportId: item.id,
        lostReportId: lostReportId,
        claimantAnswers
      });
      setSubmittedSuccess(true);
      setTimeout(() => {
        setSubmittedSuccess(false);
        onSuccess();
        onClose();
      }, 1800);
    } catch (err: any) {
      console.error(err);
      setErrorMsg(err.message || 'Failed to submit claim.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-white rounded-3xl max-w-xl w-full p-6 sm:p-8 shadow-2xl border border-slate-200 relative animate-in fade-in zoom-in-95 duration-200">
        {/* Close Button */}
        <button
          onClick={onClose}
          className="absolute top-5 right-5 p-2 text-slate-400 hover:text-slate-600 rounded-full hover:bg-slate-100 transition-colors"
        >
          <X className="w-5 h-5" />
        </button>

        {submittedSuccess ? (
          <div className="text-center py-8">
            <div className="w-16 h-16 bg-emerald-100 text-emerald-600 rounded-full flex items-center justify-center mx-auto mb-4 animate-bounce">
              <CheckCircle2 className="w-10 h-10" />
            </div>
            <h2 className="text-2xl font-black text-slate-900">Claim Submitted Successfully!</h2>
            <p className="text-xs text-slate-600 mt-2 max-w-sm mx-auto">
              Our AI evaluated your verification consistency. Campus staff will review and approve your claim shortly for QR collection.
            </p>
          </div>
        ) : (
          <form onSubmit={handleSubmit}>
            {/* Header */}
            <div className="flex items-center space-x-3 mb-4">
              <div className="w-10 h-10 rounded-2xl bg-amber-100 text-amber-800 flex items-center justify-center flex-shrink-0">
                <ShieldCheck className="w-6 h-6 text-amber-700" />
              </div>
              <div>
                <span className="text-[10px] font-black text-amber-700 uppercase tracking-widest">Anti-Fraud Verification</span>
                <h2 className="text-xl font-bold text-slate-900">Claim This Found Item</h2>
              </div>
            </div>

            {/* Target Item Pill */}
            <div className="p-3 bg-slate-50 rounded-2xl border border-slate-200 flex items-center space-x-3 mb-6">
              {item.primaryImageUrl && (
                <img
                  src={item.primaryImageUrl}
                  alt={item.title}
                  className="w-12 h-12 rounded-xl object-cover border border-slate-200"
                />
              )}
              <div className="flex-1 min-w-0">
                <p className="text-xs font-bold text-slate-900 truncate">{item.title}</p>
                <p className="text-[11px] text-slate-500 font-mono">
                  Ref: {item.referenceId} • Location: {item.locationName}
                </p>
              </div>
            </div>

            {errorMsg && (
              <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-xl text-xs text-red-800 flex items-start space-x-2">
                <AlertTriangle className="w-4 h-4 text-red-600 flex-shrink-0 mt-0.5" />
                <span>{errorMsg}</span>
              </div>
            )}

            {/* Blind Verification Instructions */}
            <div className="mb-4">
              <label className="block text-xs font-bold text-slate-800 mb-1">
                Provide Secret Identifying Details *
              </label>
              <p className="text-[11px] text-slate-500 mb-2 leading-relaxed">
                To confirm this item is yours and prevent fraudulent claims, describe items or markings that are
                <strong> NOT visible in the public photo</strong>:
              </p>
              <ul className="text-[11px] text-slate-600 space-y-1 mb-3 pl-2">
                <li>• What was stored inside compartments / pockets?</li>
                <li>• What stickers, engravings, or keychains are attached?</li>
                <li>• What specific brand, cards, or notes are inside?</li>
              </ul>
              <textarea
                rows={4}
                required
                value={claimantAnswers}
                onChange={(e) => setClaimantAnswers(e.target.value)}
                placeholder="e.g. Inside the main pocket there is a blue spiral notebook with 'Data Structures' on the cover, a Dell 65W charger, and mints in the side pocket."
                className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-xs focus:outline-none focus:ring-2 focus:ring-amber-500 font-medium leading-relaxed"
              />
            </div>

            {/* AI Assistant Note */}
            <div className="p-3 bg-indigo-50/70 rounded-xl border border-indigo-100 text-[11px] text-indigo-900 flex items-start space-x-2 mb-6">
              <Sparkles className="w-4 h-4 text-indigo-600 flex-shrink-0 mt-0.5" />
              <span>
                AI assists staff by calculating consistency against private metadata, but authorized campus personnel perform final approval.
              </span>
            </div>

            {/* Action Buttons */}
            <div className="flex items-center justify-end space-x-3">
              <button
                type="button"
                onClick={onClose}
                className="px-5 py-2.5 rounded-xl border border-slate-300 text-slate-700 text-xs font-bold hover:bg-slate-100 transition-colors"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={isSubmitting}
                className="px-6 py-2.5 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-600 hover:from-emerald-600 hover:to-teal-700 text-white text-xs font-bold shadow-md transition-all flex items-center space-x-1.5"
              >
                {isSubmitting ? (
                  <>
                    <RefreshCw className="w-3.5 h-3.5 animate-spin" />
                    <span>Evaluating Answers...</span>
                  </>
                ) : (
                  <>
                    <span>Submit Claim for Review</span>
                    <ArrowRight className="w-3.5 h-3.5" />
                  </>
                )}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
};
