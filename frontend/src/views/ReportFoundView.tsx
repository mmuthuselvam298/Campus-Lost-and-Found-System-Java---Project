import React, { useState } from 'react';
import {
  UploadCloud,
  Sparkles,
  Camera,
  CheckCircle2,
  AlertTriangle,
  HelpCircle,
  Eye,
  ShieldCheck,
  MapPin,
  Clock,
  Layers,
  ArrowRight,
  Info,
  RefreshCw,
  Edit3,
  Check
} from 'lucide-react';
import { api } from '../services/api';
import { CampusLocation, ItemCategory, AiVisionAnalysis } from '../types';

interface ReportFoundViewProps {
  locations: CampusLocation[];
  categories: ItemCategory[];
  onSuccess: (foundId: number) => void;
  onNavigate: (view: string) => void;
}

// Preset demo items to allow instant 1-click testing
const PRESET_ITEMS = [
  {
    name: '🎒 Black Nike Backpack',
    filename: 'black_nike_backpack.jpg',
    url: 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&q=80',
    title: 'Black Nike Backpack with Red Zipper Pull',
    category: 'Bags',
    color: 'Black',
    brand: 'Nike',
    material: 'Durable Fabric & Nylon',
    distinctiveFeatures: 'Small red keychain attached to side zip',
    visibleText: 'Nike Swoosh emblem',
    secretDetails: 'Contains blue spiral notebook titled Data Structures and 65W charger.',
    locationName: 'Central Library',
    specificArea: '2nd Floor Quiet Study Cubicles'
  },
  {
    name: '💧 Hydro Flask Bottle',
    filename: 'hydro_flask_blue.jpg',
    url: 'https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=800&q=80',
    title: 'Blue Insulated Hydro Flask 32oz',
    category: 'Bottles',
    color: 'Blue / Navy',
    brand: 'Hydro Flask',
    material: 'Stainless Steel',
    distinctiveFeatures: 'GitHub Octocat sticker and slight dent at base',
    visibleText: 'Hydro Flask',
    secretDetails: 'Engraved with initials SRM and black silicone base boot.',
    locationName: 'Sports Complex',
    specificArea: 'Basketball Court Row 3'
  },
  {
    name: '🪪 Student ID Card',
    filename: 'student_id_badge.jpg',
    url: 'https://images.unsplash.com/photo-1578852612716-854e527abf50?w=800&q=80',
    title: 'Student ID Badge on Blue Lanyard',
    category: 'ID Cards',
    color: 'Blue / Navy',
    brand: 'Campus Services',
    material: 'Laminated PVC Card',
    distinctiveFeatures: 'Blue breakaway lanyard with university emblem',
    visibleText: 'ID ********* [Masked for Privacy]',
    secretDetails: 'Student name Alex Chen, Department Computer Science.',
    locationName: 'Academic Block A',
    specificArea: 'Main entrance turnstiles'
  },
  {
    name: '🧮 TI-84 Calculator',
    filename: 'ti84_calculator.jpg',
    url: 'https://images.unsplash.com/photo-1594980596870-8aa52a78d8cd?w=800&q=80',
    title: 'TI-84 Plus Graphing Calculator',
    category: 'Electronics',
    color: 'Black',
    brand: 'Texas Instruments',
    material: 'Molded Plastic',
    distinctiveFeatures: 'Calculus formula sticker on inside cover',
    visibleText: 'TI-84 Plus',
    secretDetails: 'Slide cover has small initials etched in right corner.',
    locationName: 'Academic Block A',
    specificArea: 'Room 304 Desk 12'
  }
];

export const ReportFoundView: React.FC<ReportFoundViewProps> = ({
  locations,
  categories,
  onSuccess,
  onNavigate
}) => {
  const [currentStep, setCurrentStep] = useState<number>(1); // 1 Upload, 2 AI Analysis, 3 Confirm, 4 Location, 5 Submit
  const [selectedImage, setSelectedImage] = useState<File | null>(null);
  const [imagePreviewUrl, setImagePreviewUrl] = useState<string | null>(null);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [analysisStage, setAnalysisStage] = useState<string>('Looking at the item...');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [isEditingAiDetails, setIsEditingAiDetails] = useState(false);

  // Form Fields (populated by AI)
  const [title, setTitle] = useState('');
  const [category, setCategory] = useState('');
  const [color, setColor] = useState('');
  const [brand, setBrand] = useState('');
  const [material, setMaterial] = useState('');
  const [distinctiveFeatures, setDistinctiveFeatures] = useState('');
  const [visibleText, setVisibleText] = useState('');
  const [publicDescription, setPublicDescription] = useState('');
  const [privateVerificationDetails, setPrivateVerificationDetails] = useState('');
  const [locationId, setLocationId] = useState<number>(locations[0]?.id || 1);
  const [specificArea, setSpecificArea] = useState('');
  const [possessionStatus, setPossessionStatus] = useState<'FINDER_HOLDING' | 'HANDED_TO_OFFICE' | 'STORED_IN_OFFICE'>('FINDER_HOLDING');
  const [aiConfidence, setAiConfidence] = useState<number | undefined>(undefined);
  const [aiAnalysisResult, setAiAnalysisResult] = useState<AiVisionAnalysis | null>(null);

  // Handle local file selection
  const handleFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      const file = e.target.files[0];
      setSelectedImage(file);
      setImagePreviewUrl(URL.createObjectURL(file));
      await runAiAnalysis(file);
    }
  };

  // Run AI Vision Analysis with progressive human-friendly stages
  const runAiAnalysis = async (file: File, contextHint?: string) => {
    setIsAnalyzing(true);
    setCurrentStep(2);
    setErrorMsg(null);
    try {
      setAnalysisStage('Looking at the item...');
      await new Promise(r => setTimeout(r, 450));
      setAnalysisStage('Identifying category & color...');
      await new Promise(r => setTimeout(r, 450));
      setAnalysisStage('Reading visible clues & brands...');
      await new Promise(r => setTimeout(r, 450));
      setAnalysisStage('Checking image quality...');

      const result = await api.analyzeImage(file, contextHint || file.name);
      const a = result.analysis;
      setAiAnalysisResult(a);
      setImagePreviewUrl(result.imageUrl);

      setTitle(a.color ? `${a.color} ${a.category} (${a.brand || 'Item'})` : `${a.category}`);
      setCategory(a.category || 'Other');
      setColor(a.color || 'Dark Tone');
      setBrand(a.brand || '');
      setMaterial(a.material || '');
      setDistinctiveFeatures(a.distinctiveFeatures || '');
      setVisibleText(a.visibleText || '');
      setPublicDescription(`Found a ${a.color} ${a.category} with ${a.distinctiveFeatures || 'clean exterior'}. Located around campus.`);
      setAiConfidence(a.confidenceScore || 0.92);

      if (a.locationSuggestion) {
        const matchedLoc = locations.find(l => l.name.toLowerCase().includes(a.locationSuggestion.toLowerCase()));
        if (matchedLoc) {
          setLocationId(matchedLoc.id);
          setSpecificArea(a.locationReason || 'General Area');
        }
      }

      setCurrentStep(3); // Advance to Confirm
    } catch (err: any) {
      console.error(err);
      setErrorMsg('AI assistance is temporarily unavailable. You can continue manually.');
      setCurrentStep(3);
    } finally {
      setIsAnalyzing(false);
    }
  };

  // Quick Preset Simulator
  const loadPreset = async (preset: typeof PRESET_ITEMS[0]) => {
    setImagePreviewUrl(preset.url);
    setIsAnalyzing(true);
    setCurrentStep(2);

    setAnalysisStage('Looking at the item...');
    await new Promise(r => setTimeout(r, 350));
    setAnalysisStage('Identifying category & color...');
    await new Promise(r => setTimeout(r, 350));
    setAnalysisStage('Reading visible clues & brands...');
    await new Promise(r => setTimeout(r, 350));
    setAnalysisStage('Checking image quality...');
    await new Promise(r => setTimeout(r, 300));

    setTitle(preset.title);
    setCategory(preset.category);
    setColor(preset.color);
    setBrand(preset.brand);
    setMaterial(preset.material);
    setDistinctiveFeatures(preset.distinctiveFeatures);
    setVisibleText(preset.visibleText);
    setPublicDescription(`Found ${preset.title} left near ${preset.locationName}.`);
    setPrivateVerificationDetails(preset.secretDetails);
    setSpecificArea(preset.specificArea);

    const loc = locations.find(l => l.name.toLowerCase().includes(preset.locationName.toLowerCase()));
    if (loc) setLocationId(loc.id);

    setAiConfidence(0.95);
    setAiAnalysisResult({
      category: preset.category,
      subcategory: 'Campus Gear',
      color: preset.color,
      material: preset.material,
      brand: preset.brand,
      visibleText: preset.visibleText,
      distinctiveFeatures: preset.distinctiveFeatures,
      observedFeatures: [`Observed Color: ${preset.color}`, `Material: ${preset.material}`, `Marking: ${preset.visibleText}`],
      inferredFeatures: [`Suggested Location: ${preset.locationName}`, 'Inferred daily student gear'],
      confidenceScore: 0.95,
      confidenceLevel: 'High confidence',
      verificationQuestions: ['What is inside the compartments?', 'Describe any stickers or scratches.'],
      locationSuggestion: preset.locationName,
      locationReason: 'Visual interior and environment lighting cues.',
      imageQuality: { adequate: true, lightingCondition: 'Good', clarity: 'Sharp', suggestion: 'High image quality.' }
    });

    setIsAnalyzing(false);
    setCurrentStep(3);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!title || !category || !locationId) {
      setErrorMsg('Please ensure title, category, and campus location are filled.');
      return;
    }

    setIsSubmitting(true);
    setErrorMsg(null);

    try {
      const payload = {
        title,
        category,
        color,
        brand,
        material,
        distinctiveFeatures,
        visibleText,
        publicDescription,
        privateVerificationDetails,
        locationId,
        specificArea,
        foundDate: new Date().toISOString(),
        possessionStatus,
        primaryImageUrl: imagePreviewUrl || 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800&q=80',
        thumbnailImageUrl: imagePreviewUrl || 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400&q=80',
        aiConfidence: aiConfidence || 0.90
      };

      const result = await api.createFoundReport(payload);
      onSuccess(result.id);
      onNavigate('matches');
    } catch (err: any) {
      console.error(err);
      setErrorMsg(err.message || 'Failed to submit found report');
    } finally {
      setIsSubmitting(false);
    }
  };

  const stepLabels = [
    { num: 1, title: 'Upload' },
    { num: 2, title: 'AI Analysis' },
    { num: 3, title: 'Confirm' },
    { num: 4, title: 'Location' },
    { num: 5, title: 'Submit' }
  ];

  return (
    <div className="max-w-4xl mx-auto px-4 py-10">
      {/* Header Banner */}
      <div className="text-center max-w-2xl mx-auto mb-8">
        <div className="inline-flex items-center space-x-2 bg-emerald-50 text-emerald-700 px-3.5 py-1.5 rounded-full text-xs font-bold mb-3 border border-emerald-200">
          <Sparkles className="w-4 h-4 text-emerald-600" />
          <span>Multimodal AI Vision Assisted Reporting</span>
        </div>
        <h1 className="text-3xl sm:text-4xl font-black text-slate-900 tracking-tight">
          Report a Found Item
        </h1>
        <p className="mt-2 text-sm text-slate-600">
          Snap a photo and let AI identify category, brand, and markings. Review the detected details and submit in seconds.
        </p>
      </div>

      {/* Colorful 5-Step Indicator */}
      <div className="mb-8 p-4 bg-white rounded-3xl border border-slate-200/90 shadow-sm">
        <div className="grid grid-cols-5 gap-2">
          {stepLabels.map((s) => {
            const isCurrent = currentStep === s.num;
            const isCompleted = currentStep > s.num;
            return (
              <div key={s.num} className="flex flex-col items-center text-center">
                <div
                  className={`w-9 h-9 rounded-2xl flex items-center justify-center font-black text-xs transition-all ${
                    isCompleted
                      ? 'bg-emerald-500 text-white shadow-sm shadow-emerald-500/30'
                      : isCurrent
                      ? 'bg-sky-500 text-white shadow-md shadow-sky-500/30 ring-4 ring-sky-100'
                      : 'bg-slate-100 text-slate-400'
                  }`}
                >
                  {isCompleted ? <Check className="w-4 h-4" /> : s.num}
                </div>
                <span className={`text-[11px] font-bold mt-1.5 ${isCurrent ? 'text-sky-700' : isCompleted ? 'text-emerald-700' : 'text-slate-400'}`}>
                  {s.title}
                </span>
              </div>
            );
          })}
        </div>
      </div>

      {errorMsg && (
        <div className="mb-6 p-4 rounded-2xl bg-amber-50 border border-amber-200 flex items-start space-x-3 text-amber-800 text-sm">
          <AlertTriangle className="w-5 h-5 flex-shrink-0 text-amber-600 mt-0.5" />
          <div>
            <p className="font-bold">Notice</p>
            <p>{errorMsg}</p>
          </div>
        </div>
      )}

      {/* 1-Click Demo Presets */}
      <div className="mb-8 p-4 bg-white rounded-2xl border border-slate-200/90 shadow-sm">
        <div className="flex items-center justify-between mb-3">
          <span className="text-xs font-bold text-slate-700 uppercase tracking-wider flex items-center space-x-1.5">
            <Sparkles className="w-3.5 h-3.5 text-sky-600" />
            <span>Try 1-Click Sample Items</span>
          </span>
          <span className="text-[11px] text-slate-400">Test AI vision without a camera</span>
        </div>
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-2.5">
          {PRESET_ITEMS.map((item, idx) => (
            <button
              key={idx}
              type="button"
              onClick={() => loadPreset(item)}
              className="p-2.5 rounded-xl border border-slate-200 hover:border-sky-400 hover:bg-sky-50/50 text-left text-xs font-bold text-slate-800 transition-all flex items-center space-x-2"
            >
              <span>{item.name}</span>
            </button>
          ))}
        </div>
      </div>

      <form onSubmit={handleSubmit} className="space-y-8">
        {/* STEP 1: Upload Photo */}
        <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <div>
              <span className="text-xs font-black text-sky-600 uppercase tracking-widest">Step 1</span>
              <h2 className="text-lg font-bold text-slate-900">Upload Photo of Found Item</h2>
            </div>
            {aiConfidence && (
              <div className="inline-flex items-center space-x-1.5 bg-emerald-50 text-emerald-700 px-3 py-1 rounded-full text-xs font-bold border border-emerald-200">
                <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />
                <span>Confidence: {Math.round(aiConfidence * 100)}%</span>
              </div>
            )}
          </div>

          <div className="relative border-2 border-dashed border-slate-300 hover:border-sky-500 rounded-3xl p-6 transition-all bg-slate-50/50 text-center">
            {imagePreviewUrl ? (
              <div className="relative inline-block max-w-sm rounded-2xl overflow-hidden border border-slate-200 shadow-md">
                <img
                  src={imagePreviewUrl}
                  alt="Item Preview"
                  className="w-full h-64 object-cover"
                />

                {/* Subtle AI Scanning Progress Overlay */}
                {isAnalyzing && (
                  <div className="absolute inset-0 bg-slate-900/60 backdrop-blur-[2px] flex flex-col items-center justify-center text-white p-4">
                    <div className="w-12 h-12 rounded-full border-4 border-sky-400 border-t-transparent animate-spin mb-3" />
                    <p className="font-bold text-sm tracking-wide text-sky-200">{analysisStage}</p>
                    <div className="w-48 h-1.5 bg-white/20 rounded-full overflow-hidden mt-3">
                      <div className="w-full h-full bg-sky-400 animate-pulse" />
                    </div>
                  </div>
                )}

                <label className="absolute bottom-3 right-3 bg-white/95 backdrop-blur hover:bg-white text-slate-800 text-xs font-bold px-3 py-1.5 rounded-xl shadow cursor-pointer transition-colors flex items-center space-x-1.5 border border-slate-200">
                  <Edit3 className="w-3.5 h-3.5 text-sky-600" />
                  <span>Change Photo</span>
                  <input type="file" accept="image/*" onChange={handleFileChange} className="hidden" />
                </label>
              </div>
            ) : (
              <div className="py-12 flex flex-col items-center justify-center">
                <div className="w-16 h-16 rounded-2xl bg-sky-50 text-sky-600 flex items-center justify-center mb-4">
                  <UploadCloud className="w-8 h-8" />
                </div>
                <h3 className="font-bold text-slate-800 text-base">Drag & drop photo here, or browse</h3>
                <p className="text-xs text-slate-500 mt-1 max-w-sm">
                  Supports JPEG, PNG, WEBP. Photo is automatically evaluated by multimodal AI vision to detect attributes.
                </p>
                <label className="mt-5 px-6 py-3 rounded-2xl bg-gradient-to-r from-sky-500 to-blue-600 hover:from-sky-600 hover:to-blue-700 text-white text-xs font-bold shadow-md cursor-pointer transition-all flex items-center space-x-2">
                  <Camera className="w-4 h-4" />
                  <span>Take or Select Photo</span>
                  <input type="file" accept="image/*" onChange={handleFileChange} className="hidden" />
                </label>
              </div>
            )}
          </div>

          {/* AI DETECTED Card with Confirm / Edit */}
          {aiAnalysisResult && (
            <div className="mt-6 p-5 rounded-2xl bg-gradient-to-br from-sky-50/50 to-indigo-50/40 border border-sky-100">
              <div className="flex items-center justify-between mb-3">
                <span className="text-xs font-black text-sky-800 flex items-center space-x-1.5 uppercase tracking-wider">
                  <Sparkles className="w-4 h-4 text-sky-600" />
                  <span>AI DETECTED</span>
                </span>
                <div className="flex items-center space-x-2">
                  <button
                    type="button"
                    onClick={() => setIsEditingAiDetails(!isEditingAiDetails)}
                    className="px-3 py-1 bg-white hover:bg-slate-50 rounded-xl border border-slate-200 text-xs font-bold text-slate-700 flex items-center space-x-1"
                  >
                    <Edit3 className="w-3.5 h-3.5 text-slate-500" />
                    <span>{isEditingAiDetails ? 'Done' : 'Edit'}</span>
                  </button>
                  <button
                    type="button"
                    onClick={() => setCurrentStep(4)}
                    className="px-3 py-1 bg-emerald-500 hover:bg-emerald-600 text-white rounded-xl text-xs font-bold flex items-center space-x-1 shadow-sm"
                  >
                    <Check className="w-3.5 h-3.5" />
                    <span>Confirm</span>
                  </button>
                </div>
              </div>

              {/* Detected Chips */}
              <div className="flex flex-wrap gap-2 mb-4">
                {category && (
                  <span className="px-3 py-1 rounded-xl bg-white border border-slate-200 text-xs font-bold text-slate-800 shadow-sm">
                    Category: <strong className="text-sky-700">{category}</strong>
                  </span>
                )}
                {color && (
                  <span className="px-3 py-1 rounded-xl bg-white border border-slate-200 text-xs font-bold text-slate-800 shadow-sm">
                    Color: <strong className="text-indigo-700">{color}</strong>
                  </span>
                )}
                {brand && (
                  <span className="px-3 py-1 rounded-xl bg-white border border-slate-200 text-xs font-bold text-slate-800 shadow-sm">
                    Brand: <strong className="text-teal-700">{brand}</strong>
                  </span>
                )}
                {distinctiveFeatures && (
                  <span className="px-3 py-1 rounded-xl bg-white border border-slate-200 text-xs font-bold text-slate-800 shadow-sm">
                    Distinctive: <strong className="text-emerald-700">{distinctiveFeatures}</strong>
                  </span>
                )}
              </div>

              {/* Observed vs Inferred breakdown */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-xs">
                <div className="p-3.5 bg-white rounded-xl border border-emerald-100 shadow-sm">
                  <p className="font-bold text-emerald-800 flex items-center space-x-1 text-[11px] uppercase tracking-wider mb-2">
                    <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />
                    <span>Directly Observed Facts</span>
                  </p>
                  <ul className="space-y-1 text-slate-600">
                    {aiAnalysisResult.observedFeatures.map((f, i) => (
                      <li key={i}>• {f}</li>
                    ))}
                  </ul>
                </div>

                <div className="p-3.5 bg-white rounded-xl border border-sky-100 shadow-sm">
                  <p className="font-bold text-sky-800 flex items-center space-x-1 text-[11px] uppercase tracking-wider mb-2">
                    <Info className="w-3.5 h-3.5 text-sky-600" />
                    <span>Inferred Context & Clues</span>
                  </p>
                  <ul className="space-y-1 text-slate-600">
                    {aiAnalysisResult.inferredFeatures.map((f, i) => (
                      <li key={i}>• {f}</li>
                    ))}
                  </ul>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* STEP 3: Confirm & Edit Details */}
        <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-sm">
          <div className="mb-6">
            <span className="text-xs font-black text-sky-600 uppercase tracking-widest">Step 3</span>
            <h2 className="text-lg font-bold text-slate-900">Item Description Details</h2>
            <p className="text-xs text-slate-500">Confirm or adjust the information that will be publicly visible to searchers.</p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
            <div className="sm:col-span-2">
              <label className="block text-xs font-bold text-slate-700 mb-1.5">Item Title / Summary *</label>
              <input
                type="text"
                required
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="e.g. Black Nike Backpack with Red Zipper Pull"
                className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 font-medium"
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1.5">Category *</label>
              <select
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 font-medium"
              >
                {categories.map((c) => (
                  <option key={c.id} value={c.name}>{c.name}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1.5">Color</label>
              <input
                type="text"
                value={color}
                onChange={(e) => setColor(e.target.value)}
                placeholder="e.g. Black, Navy, Silver"
                className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 font-medium"
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1.5">Brand / Logo</label>
              <input
                type="text"
                value={brand}
                onChange={(e) => setBrand(e.target.value)}
                placeholder="e.g. Nike, Apple, Hydro Flask"
                className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 font-medium"
              />
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1.5">Distinctive Features (Public)</label>
              <input
                type="text"
                value={distinctiveFeatures}
                onChange={(e) => setDistinctiveFeatures(e.target.value)}
                placeholder="e.g. Red keychain, sticker on cover"
                className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 font-medium"
              />
            </div>

            <div className="sm:col-span-2">
              <label className="block text-xs font-bold text-slate-700 mb-1.5">Public Description</label>
              <textarea
                rows={2}
                value={publicDescription}
                onChange={(e) => setPublicDescription(e.target.value)}
                className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 font-medium"
              />
            </div>
          </div>
        </div>

        {/* STEP 4: Anti-Fraud Secret Verification Details */}
        <div className="bg-amber-50/60 rounded-3xl p-6 sm:p-8 border border-amber-200 shadow-sm">
          <div className="flex items-start space-x-3 mb-4">
            <div className="w-9 h-9 rounded-2xl bg-amber-100 text-amber-800 flex items-center justify-center flex-shrink-0">
              <ShieldCheck className="w-5 h-5 text-amber-700" />
            </div>
            <div>
              <span className="text-xs font-black text-amber-800 uppercase tracking-widest">Anti-Fraud Security</span>
              <h2 className="text-lg font-bold text-slate-900">Private Verification Details (Hidden from Public)</h2>
              <p className="text-xs text-slate-600 mt-0.5">
                Enter details only the true owner would know (e.g. inner contents, student cards inside, hidden engraving).
                <strong> This will NEVER be displayed publicly.</strong>
              </p>
            </div>
          </div>

          <div>
            <textarea
              rows={3}
              value={privateVerificationDetails}
              onChange={(e) => setPrivateVerificationDetails(e.target.value)}
              placeholder="e.g. Inside the front pocket there is a blue notebook with 'Data Structures' on it, plus ₹200 cash."
              className="w-full px-3.5 py-2.5 bg-white border border-amber-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-amber-500 font-medium"
            />
          </div>
        </div>

        {/* STEP 5: Campus Location & Current Possession */}
        <div className="bg-white rounded-3xl p-6 sm:p-8 border border-slate-200/90 shadow-sm">
          <div className="mb-6">
            <span className="text-xs font-black text-sky-600 uppercase tracking-widest">Step 4</span>
            <h2 className="text-lg font-bold text-slate-900">Location & Possession</h2>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1.5">Campus Building *</label>
              <select
                value={locationId}
                onChange={(e) => setLocationId(Number(e.target.value))}
                className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 font-medium"
              >
                {locations.map((loc) => (
                  <option key={loc.id} value={loc.id}>{loc.name} ({loc.zone})</option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1.5">Specific Floor / Room</label>
              <input
                type="text"
                value={specificArea}
                onChange={(e) => setSpecificArea(e.target.value)}
                placeholder="e.g. 2nd Floor Study Room 204"
                className="w-full px-3.5 py-2.5 bg-slate-50 border border-slate-300 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-sky-500 font-medium"
              />
            </div>

            <div className="sm:col-span-2">
              <label className="block text-xs font-bold text-slate-700 mb-2">Item Possession Status</label>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <label
                  onClick={() => setPossessionStatus('FINDER_HOLDING')}
                  className={`p-3.5 rounded-2xl border cursor-pointer transition-all flex items-start space-x-3 ${
                    possessionStatus === 'FINDER_HOLDING'
                      ? 'border-sky-500 bg-sky-50/50 shadow-sm'
                      : 'border-slate-200 hover:border-slate-300'
                  }`}
                >
                  <input
                    type="radio"
                    name="possession"
                    checked={possessionStatus === 'FINDER_HOLDING'}
                    onChange={() => setPossessionStatus('FINDER_HOLDING')}
                    className="mt-0.5 text-sky-600"
                  />
                  <div>
                    <p className="text-xs font-bold text-slate-900">I am currently holding the item</p>
                    <p className="text-[11px] text-slate-500 mt-0.5">I will hand it over once the owner is verified.</p>
                  </div>
                </label>

                <label
                  onClick={() => setPossessionStatus('HANDED_TO_OFFICE')}
                  className={`p-3.5 rounded-2xl border cursor-pointer transition-all flex items-start space-x-3 ${
                    possessionStatus === 'HANDED_TO_OFFICE'
                      ? 'border-sky-500 bg-sky-50/50 shadow-sm'
                      : 'border-slate-200 hover:border-slate-300'
                  }`}
                >
                  <input
                    type="radio"
                    name="possession"
                    checked={possessionStatus === 'HANDED_TO_OFFICE'}
                    onChange={() => setPossessionStatus('HANDED_TO_OFFICE')}
                    className="mt-0.5 text-sky-600"
                  />
                  <div>
                    <p className="text-xs font-bold text-slate-900">Handed to Central Lost & Found Office</p>
                    <p className="text-[11px] text-slate-500 mt-0.5">Item is safely stored in central campus storage.</p>
                  </div>
                </label>
              </div>
            </div>
          </div>
        </div>

        {/* STEP 5: Final Submission */}
        <div className="flex items-center justify-end space-x-4 pt-4">
          <button
            type="button"
            onClick={() => onNavigate('browse')}
            className="px-6 py-3 rounded-2xl border border-slate-300 text-slate-700 text-sm font-bold hover:bg-slate-100 transition-colors"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={isSubmitting || isAnalyzing}
            className="px-8 py-3.5 rounded-2xl bg-gradient-to-r from-emerald-500 to-teal-600 hover:from-emerald-600 hover:to-teal-700 text-white text-sm font-bold shadow-lg shadow-emerald-500/25 hover:shadow-emerald-500/40 hover:-translate-y-0.5 active:translate-y-0 transition-all flex items-center space-x-2"
          >
            {isSubmitting ? (
              <>
                <RefreshCw className="w-4 h-4 animate-spin" />
                <span>Publishing & Matching...</span>
              </>
            ) : (
              <>
                <span>Publish Found Report</span>
                <ArrowRight className="w-4 h-4" />
              </>
            )}
          </button>
        </div>
      </form>
    </div>
  );
};
