import React from 'react';
import {
  X,
  MapPin,
  Calendar,
  ShieldCheck,
  Tag,
  CheckCircle2,
  Lock,
  Building,
  ArrowRight
} from 'lucide-react';
import { FoundReport } from '../types';

interface ItemDetailModalProps {
  item: FoundReport | null;
  isOpen: boolean;
  onClose: () => void;
  onClaim: (item: FoundReport) => void;
}

export const ItemDetailModal: React.FC<ItemDetailModalProps> = ({
  item,
  isOpen,
  onClose,
  onClaim
}) => {
  if (!isOpen || !item) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-4">
      <div className="bg-white rounded-3xl max-w-2xl w-full p-6 sm:p-8 shadow-2xl border border-slate-200 relative animate-in fade-in zoom-in-95 duration-200">
        <button
          onClick={onClose}
          className="absolute top-5 right-5 p-2 text-slate-400 hover:text-slate-600 rounded-full hover:bg-slate-100 transition-colors"
        >
          <X className="w-5 h-5" />
        </button>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 mb-6">
          {/* Item Image */}
          <div className="h-64 rounded-2xl overflow-hidden bg-slate-100 border border-slate-200">
            <img
              src={item.primaryImageUrl || 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&q=80'}
              alt={item.title}
              className="w-full h-full object-cover"
            />
          </div>

          {/* Details */}
          <div className="flex flex-col justify-between">
            <div>
              <div className="flex items-center space-x-2 text-xs font-mono font-bold text-slate-500 mb-1">
                <span>Ref: {item.referenceId}</span>
                <span className="text-brand-600">• {item.category}</span>
              </div>

              <h2 className="text-xl font-bold text-slate-900 mb-2">{item.title}</h2>
              <p className="text-xs text-slate-600 leading-relaxed mb-4">{item.publicDescription}</p>

              <div className="space-y-1.5 text-xs text-slate-700">
                <div className="flex items-center space-x-2">
                  <MapPin className="w-3.5 h-3.5 text-red-500 flex-shrink-0" />
                  <span className="font-bold">{item.locationName}</span>
                  {item.specificArea && <span className="text-slate-500">({item.specificArea})</span>}
                </div>

                <div className="flex items-center space-x-2 text-slate-600">
                  <Calendar className="w-3.5 h-3.5 text-slate-400 flex-shrink-0" />
                  <span>Found: {new Date(item.foundDate).toLocaleString()}</span>
                </div>

                {item.storageLocation && (
                  <div className="flex items-center space-x-2 text-slate-600">
                    <Building className="w-3.5 h-3.5 text-amber-600 flex-shrink-0" />
                    <span>Physical Storage: <strong>{item.storageLocation}</strong></span>
                  </div>
                )}
              </div>
            </div>

            {/* Privacy Alert */}
            <div className="mt-4 p-3 rounded-xl bg-amber-50 border border-amber-200 text-amber-900 text-xs flex items-start space-x-2">
              <Lock className="w-4 h-4 text-amber-600 flex-shrink-0 mt-0.5" />
              <span>
                <strong>Anti-Fraud Protection:</strong> Sensitive contents and verifying markings are masked from public display.
              </span>
            </div>
          </div>
        </div>

        {/* Footer Actions */}
        <div className="flex items-center justify-end space-x-3 pt-4 border-t border-slate-100">
          <button
            onClick={onClose}
            className="px-5 py-2.5 rounded-xl border border-slate-300 text-slate-700 text-xs font-bold hover:bg-slate-100"
          >
            Close
          </button>
          {item.status !== 'RETURNED' && (
            <button
              onClick={() => {
                onClose();
                onClaim(item);
              }}
              className="px-6 py-2.5 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-600 text-white text-xs font-bold shadow-md hover:from-emerald-600 hover:to-teal-700 flex items-center space-x-1.5"
            >
              <CheckCircle2 className="w-4 h-4" />
              <span>Claim This Item</span>
            </button>
          )}
        </div>
      </div>
    </div>
  );
};
